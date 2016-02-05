package br.ufscar.sead.loa.propeller

import br.ufscar.sead.loa.propeller.domain.ProcessDefinition
import br.ufscar.sead.loa.propeller.tmp.User
import com.mongodb.DuplicateKeyException
import com.mongodb.MongoClient
import org.bson.Document
import org.bson.types.ObjectId
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Morphia

import java.util.logging.Logger
import java.util.logging.Level

/**
*  Created by matheus on 1/28/16.
*  https://github.com/matheuss
*/
@Singleton
class Propeller {
    Map options
    boolean configured
    Datastore ds

    /**
     *
     * @param options: dbName, wipeDb, userId
     * @return
     */

    Propeller init(Map options) {
        if (this.configured) {
            return this
        }
        this.configured = true

        if (options.dbName == null) {
            throw new MissingPropertyException('dbName is required')
        }

        // disable annoying Mongo logs
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);

        Morphia morphia = new Morphia()

        morphia.mapPackage("br.ufscar.sead.loa.propeller.domain")
        this.ds = morphia.createDatastore(new MongoClient(), options.dbName as String)
        this.ds.ensureIndexes()

        if (options.wipeDb == true) {
            this.ds.getDB().dropDatabase()
        }

        this.options = options

        return this
    }

    def deploy(File json) {
        deploy(json.text)
    }

    /**
     * TODO: improve error/success return values/logic
     * idea: return a ProcessDefinition with #getErrors() #deployed() etc.
     * @see br.ufscar.sead.loa.propeller.domain.ProcessDefinition
     */
    ProcessDefinition deploy(String json) {
        def doc = Document.parse(json)
        def pd = new ProcessDefinition(doc)

        if (pd.validate()) {
            try {
                this.ds.save(pd)
                pd.deployed = true
            } catch (DuplicateKeyException ignored) {
                // TODO: set errors in ProcessDefinition instance
            }
        }

        return pd
    }

    /**
     * TODO: find a way to allow task delegation here
     * @param uri
     * @param owner
     * @return
     */
    def instantiate(String uri, Object owner) {
        return // Temporary – code below doesn't work anymore

        def process = this.db.getCollection('process_definition').find(new Document("uri", uri)).first()

        if (!process) {
            return Errors.PROCESS_NOT_FOUND
        }

        // Create a ID for the instance – without this, Mongo will reject a second instance
        // TODO: check if this affirmation is really true
        process.put("_id", new ObjectId())

        // When a process is instantiated, each task should have an id
        def tasks = process.get('tasks') as ArrayList<Document>
        tasks.each { task ->
            task.put('id', new ObjectId())
        }

        def idProperty = this.options.userId == null? "id" : this.options.userId
        process.put('ownerId', owner[idProperty])

        db.getCollection("process_instance").insertOne(process)
    }

    def static main(args) {
        Propeller.instance.init([dbName: 'propeller', 'wipeDb': true])
        Propeller.instance.deploy(new File('spec/drafts/process.json'))
        println Propeller.instance.instantiate('forca', new User(1))
    }
}