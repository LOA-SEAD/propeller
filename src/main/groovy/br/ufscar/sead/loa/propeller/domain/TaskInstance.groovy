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
    TaskDefinition definition
    int status
    @Embedded
    ArrayList<TaskOutputInstance> outputs

    transient static int STATUS_PENDING = 1
    transient static int STATUS_COMPLETED = 2

    TaskInstance() {}

    TaskInstance(TaskDefinition definition) {
        this.id = new ObjectId()
        this.definition = definition
        this.status = STATUS_PENDING
        this.outputs = new ArrayList<>(definition.outputs.size())

        Propeller.instance.ds.save(this)
    }
}
