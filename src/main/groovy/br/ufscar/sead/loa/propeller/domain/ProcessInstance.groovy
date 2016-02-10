package br.ufscar.sead.loa.propeller.domain

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

    ProcessInstance() {}

    ProcessInstance(ProcessDefinition definition, long ownerId) {
        this.id = new ObjectId()
        this.definition = definition
        this.ownerId = ownerId
        this.pendingTasks = new ArrayList<>(definition.tasks.size())
        this.completedTasks = new ArrayList<>(definition.tasks.size())

        this.definition.tasks.each {task ->
            this.pendingTasks.add(new TaskInstance(task))
        }
    }
}