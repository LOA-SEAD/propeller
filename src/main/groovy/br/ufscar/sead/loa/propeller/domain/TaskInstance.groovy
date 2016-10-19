package br.ufscar.sead.loa.propeller.domain

import br.ufscar.sead.loa.propeller.Propeller
import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Embedded
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Field
import org.mongodb.morphia.annotations.Id
import org.mongodb.morphia.annotations.Index
import org.mongodb.morphia.annotations.Indexes
import org.mongodb.morphia.annotations.Reference

/**
 * Created by matheus on 2/8/16.
 * Last Modification by Lucas Suguinoshita on 8/29/16.
 * https://github.com/matheuss
 */
@Entity('task_instance')
@Indexes(
        @Index(value = "status", fields = @Field("status"))
)
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

    /**
     * Completes the task when receiving a varargs of paths to files that matches the expected outputs for the task.
     * The completion will fail if: paths.size() differs of what the task expects or if the files don't match what the
     * task expects.
     * Note that the paths can be in a different order from what the task expect.
     *
     * @param paths a varargs containing the paths for the files that the task expects as outputs
     *
     * @return true if the files matches what the task expects or false otherwise.
     */
    boolean complete(String... paths) {
        // A task can only be completed if it's pending
        if (this.status != STATUS_PENDING) {
            return false
        }

        this.outputs = new ArrayList<>()
        this.definition.outputs.size().times {
            // this is necessary because an array can't have 'empty' elements – you can't set a element in a array if
            // the previous element has not been set. this will happen when the paths are in a different order than
            // what's defined by the process definition
            this.outputs.add(null)
        }

        def requiredOutputs = 0
        def optionalOutputs = 0
        for (path in paths) {
            def fileName = path.substring(path.lastIndexOf('/') + 1) // extract fileName from full path
            this.definition.outputs.eachWithIndex { output, i -> // loop through
                if (output.name == fileName) {
                    if (output.optional) {
                        optionalOutputs++
                    } else {
                        requiredOutputs++
                    }
                    this.outputs.set(i, new TaskOutputInstance(output, path))
                } else {
                    return false
                }
            }
        }

        // All required outputs must have a matching path, otherwise the completion fails
        // All remaining paths must match with an optional output and must not exceed the
        // number of optional outputs defined by the task
        if (requiredOutputs != this.definition.outputs.size() && optionalOutputs <= this.definition.optionalOutputs.size()) {
            this.outputs = null
            println 'task ' + this.id + " not completed: count != expected"
            return false
        }

        this.status = STATUS_COMPLETED

        this.process.pendingTasks.remove(this)
        this.process.completedTasks.add(this)

        if ( !this.process.pendingTasks.any { task -> !(task.definition.optional) } ) {
            this.process.status = ProcessInstance.STATUS_ALL_TASKS_COMPLETED
        }

        Propeller.instance.ds.save(this.process, this)

        return true
    }
}
