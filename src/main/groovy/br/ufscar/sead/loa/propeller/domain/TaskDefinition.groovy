package br.ufscar.sead.loa.propeller.domain

import br.ufscar.sead.loa.propeller.Propeller
import br.ufscar.sead.loa.propeller.util.Helper
import br.ufscar.sead.loa.propeller.util.Mistakable
import org.bson.Document
import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Embedded
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Id

/**
 * Created by matheus on 2/4/16.
 * Last Modification by Lucas Suguinoshita on 8/29/16.
 * https://github.com/matheuss
 */

@Entity('task_definition')
class TaskDefinition extends Mistakable {

    @Id
    ObjectId id
    String name
    String uri
    String description
    String type // TODO: change to int
    Boolean optional;
    // dependencies TODO
    @Embedded
    ArrayList<TaskOutputDefinition> outputs
    ArrayList<TaskOutputDefinition> optionalOutputs


    TaskDefinition() {}

    TaskDefinition(Document doc) {
        this.id = new ObjectId()
        this.name = doc.getString("name")
        this.uri = doc.getString("uri")
        this.description = doc.getString("description")
        this.type = doc.getString("type")
        this.optional = doc.getBoolean("optional")

        ArrayList<Document> outputs = doc.get('outputs') as ArrayList<Document>

        this.outputs = new ArrayList<TaskOutputDefinition>()
        this.optionalOutputs = new ArrayList<TaskOutputDefinition>()

        outputs.each { output ->
            if(output.getBoolean("optional")) {
                this.optionalOutputs.add(new TaskOutputDefinition(output))
            } else {
                this.outputs.add(new TaskOutputDefinition(output))
            }
        }

        Propeller.instance.ds.save(this)
    }

    boolean validate() {
        if (false in [Helper.valid(this.name), Helper.valid(this.uri), Helper.valid(this.description),
                      Helper.valid(this.type)]) return false

        if (!this.outputs) return false

        def invalidOutput = this.outputs.find { output ->
            // This Closure will return null if all outputs are valid; Otherwise, will return the first invalid task
            return !output.validate()
        }

        return !invalidOutput
    }
}
