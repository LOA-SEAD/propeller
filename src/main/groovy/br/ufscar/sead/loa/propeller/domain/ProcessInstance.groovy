package br.ufscar.sead.loa.propeller.domain

import br.ufscar.sead.loa.propeller.Propeller
import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Id
import org.mongodb.morphia.annotations.Reference

/**
 * Created by matheus on 2/5/16.
 * https://github.com/matheuss
 */

@Entity('process_instance')
class ProcessInstance {
    @Id
    ObjectId id
    @Reference
    ProcessDefinition definition

    long ownerId

    @Reference
    ArrayList<TaskInstance> pendingTasks
    @Reference
    ArrayList<TaskInstance> completedTasks

    Map<String, String> vars

    ProcessInstance() {}

    ProcessInstance(ProcessDefinition definition, long ownerId) {
        this.id = new ObjectId()
        this.definition = definition
        this.ownerId = ownerId
        this.pendingTasks = new ArrayList<>(definition.tasks.size())
        this.completedTasks = new ArrayList<>(definition.tasks.size())

        this.definition.tasks.each { task ->
            this.pendingTasks.add(new TaskInstance(task, this))
        }
    }

    /**
     * Set a custom variable to the instance
     * @param key
     * @param value
     * @param save if false, the instance will not be persisted – useful when setting more than one var in a row
     */

    void putVariable(String key, String value, boolean save) {
        if (!this.vars) {
            this.vars = new HashMap<>()
        }

        this.vars.put(key, value)

        if (save) {
            Propeller.instance.ds.save(this)
        }
    }

    /**
     *
     * @param key
     * @return the variable null if it doesn't exists
     */

    String getVariable(String key) {
        if (!this.vars) {
            return null
        }
        this.vars.get(key)
    }
}