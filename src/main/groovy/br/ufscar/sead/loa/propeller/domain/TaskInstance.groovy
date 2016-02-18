package br.ufscar.sead.loa.propeller.domain

import br.ufscar.sead.loa.propeller.Propeller
import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Embedded
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Id
import org.mongodb.morphia.annotations.Reference

/**
 * Created by matheus on 2/8/16.
 * https://github.com/matheuss
 */
@Entity('task_instance')
class TaskInstance {
    @Id
    ObjectId id
    @Reference
    ProcessInstance process
    @Reference
    TaskDefinition definition
    int status
    @Embedded
    ArrayList<TaskOutputInstance> outputs

    transient static int STATUS_PENDING = 1
    transient static int STATUS_COMPLETED = 2

    Map<String, String> vars

    TaskInstance() {}

    TaskInstance(TaskDefinition definition, ProcessInstance process) {
        this.id = new ObjectId()
        this.process = process
        this.definition = definition
        this.status = STATUS_PENDING
        this.outputs = new ArrayList<>(definition.outputs.size())

        Propeller.instance.ds.save(this)
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
