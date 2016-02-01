package br.ufscar.sead.loa.propeller

import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.IndexOptions
import org.bson.Document
import org.bson.types.ObjectId

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
    MongoDatabase db

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

        def client = new MongoClient()
        this.db = client.getDatabase(options.dbName as String)

        if (options.wipeDb == true) {
            db.drop()
        }

        // uri should be an (unique) index
        db.getCollection('process_definition').createIndex(new Document('uri', 1), new IndexOptions().unique(true))

        this.options = options

        return this
    }

    def deploy(File json) {
        deploy(json.text)
    }

    /**
     * TODO: improve error/success return values/logic
     * idea: return a ProcessDefinition with #getErrors() #deployed() etc.
     * @see br.ufscar.sead.loa.propeller.ProcessDefinition
     */
    def deploy(String json) {
        def collection = this.db.getCollection('process_definition')
        def doc = Document.parse(json)

        // TODO: validation

        def uri = doc.getString('uri')

        // will be true if a process with the given uri already exists on the database
        if (collection.count(new Document('uri': uri))) {
            return Errors.PROCESS_URI_NOT_UNIQUE
        }

        doc.put('uri', uri)

        collection.insertOne(doc)
    }

    def static main(args) {
        Propeller.instance.init([dbName: 'propeller=', 'wipeDb': true])
        def r = Propeller.instance.deploy(new File('spec/drafts/process.json'))
        println r

    }
}