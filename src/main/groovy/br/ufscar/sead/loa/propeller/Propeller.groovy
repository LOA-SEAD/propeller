package br.ufscar.sead.loa.propeller

import br.ufscar.sead.loa.propeller.domain.ProcessDefinition
import br.ufscar.sead.loa.propeller.domain.ProcessInstance
import com.mongodb.DuplicateKeyException
import com.mongodb.MongoClient
import org.bson.Document
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
     * @param options : dbName, wipeDb, userId
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

        // Suppress annoying Mongo/Morphia logs
        Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE)
        Logger.getLogger("org.mongodb.morphia").setLevel(Level.SEVERE)

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

    def deploy(File json, long ownerId) {
        deploy(json.text, ownerId)
    }

    /**
     * TODO: improve error/success return values/logic
     * idea: return a ProcessDefinition with #getErrors() #deployed() etc.
     * @see br.ufscar.sead.loa.propeller.domain.ProcessDefinition
     */
    ProcessDefinition deploy(String json, long ownerId) {
        def doc = Document.parse(json)
        def pd = new ProcessDefinition(doc, ownerId)

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
    def instantiate(String uri, long ownerId) {
        def definition = this.ds.createQuery(ProcessDefinition.class).field('uri').equal(uri).get()

        if (!definition) {
            return Errors.PROCESS_NOT_FOUND
        }

        def instance = new ProcessInstance(definition, ownerId)
        this.ds.save(instance)

        return instance
    }

    def static main(args) {
        Propeller.instance.init([dbName: 'propeller', 'wipeDb': true])
        Propeller.instance.deploy(new File('spec/drafts/process.json'), 1)
        println Propeller.instance.instantiate('forca', 1)
    }
}