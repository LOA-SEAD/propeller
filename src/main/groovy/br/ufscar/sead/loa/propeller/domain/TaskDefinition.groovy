package br.ufscar.sead.loa.propeller.domain

import br.ufscar.sead.loa.propeller.Propeller
import br.ufscar.sead.loa.propeller.util.Helper
import br.ufscar.sead.loa.propeller.util.Mistakable
import org.bson.Document
import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Id

/**
 * Created by matheus on 2/4/16.
 * https://github.com/matheuss
 */

@Entity('task_definition')
class TaskDefinition extends Mistakable {

    @Id
    ObjectId id
    String name
    String description
    String type // TODO: change to int
    // dependencies TODO
    ArrayList<TaskOutput> outputs // TODO: create a TaskOutput class


    TaskDefinition() {}

    TaskDefinition(Document doc) {
        this.id = new ObjectId()
        this.name = doc.getString("name")
        this.description = doc.getString("description")
        this.type = doc.getString("type")

        ArrayList<Document> outputs = doc.get('outputs') as ArrayList<Document>

        this.outputs = new ArrayList<TaskOutput>()

        outputs.each {output ->
            this.outputs.add(new TaskOutput(output))
        }

        Propeller.instance.ds.save(this)
    }

    boolean validate() {
        if (false in [Helper.valid(this.name), Helper.valid(this.description), Helper.valid(this.type)]) return false

        if (!this.outputs) return false

        def invalidOutput = this.outputs.find { output ->
            // This Closure will return null if all outputs are valid; Otherwise, will return the first invalid task
            return !output.validate()
        }

        return !invalidOutput
    }
}
