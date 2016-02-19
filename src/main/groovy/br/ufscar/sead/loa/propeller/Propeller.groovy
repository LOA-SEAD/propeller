package br.ufscar.sead.loa.propeller

import br.ufscar.sead.loa.propeller.domain.ProcessDefinition
import br.ufscar.sead.loa.propeller.domain.ProcessInstance
import br.ufscar.sead.loa.propeller.domain.TaskInstance
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
     * @param options : dbName, wipeDb, userId
     * @return
     */

    Propeller init(Map options) {
        if (this.configured) {
            return this
        }

        if (options.dbName == null) {
            throw new MissingPropertyException('dbName is required')
        }

        this.configured = true

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

    /**
     * Find a task based on a given id
     *
     * @param taskId the desired task's id
     * @param userId the id of the user that is trying to access the task
     * @return the task or null if: no task have such id; the userId != task.process.ownerId
     * @throws IllegalArgumentException if taskId is null/invalid (See <a href="URL#http://api.mongodb.org/java/current/org/bson/types/ObjectId.html">MongoDB docs</a>)
     */

    TaskInstance getTaskInstance(String taskId, long userId) {
        ObjectId id
        try {
            id = new ObjectId(taskId)
        } catch (Exception ignored) {
            throw new IllegalArgumentException("Invalid taskId")
        }

        def task = this.ds.createQuery(TaskInstance.class).field('id').equal(id).get()

        if (!task || task.process.ownerId != userId) {
            return null
        }

        return task
    }

    /**
     * Find all processes that belongs to a given user
     *
     * @param ownerId
     * @return a list containing all tasks owner by that user or an empty list
     */
    ArrayList<ProcessInstance> getProcessesInstanceByOwner(long ownerId) {
        return this.ds.createQuery(ProcessInstance.class).field('ownerId').equal(ownerId).asList()
    }

    /**
     *
     * @param id the desired process instance id
     * @param userId the id of the user that is requiring the instance.
     * @return the instance if the user id matches the owner id or if userId == 0 ('admin' mode); null if the no such
     *         instance exists or belongs to someone else
     */
    ProcessInstance getProcessInstanceById(ObjectId id, long userId) {
        def instance = this.ds.createQuery(ProcessInstance.class).field('id').equal(id).get()

        if (!instance) {
            return null
        }

        if (instance.ownerId != userId && userId != 0) {
            return null
        }

        return instance
    }

    def static main(args) {}
}