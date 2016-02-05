package br.ufscar.sead.loa.propeller.domain

import br.ufscar.sead.loa.propeller.util.Helper
import br.ufscar.sead.loa.propeller.util.Mistakable
import org.bson.Document
import org.mongodb.morphia.annotations.Embedded

/**
 * Created by matheus on 2/4/16.
 * https://github.com/matheuss
 */

@Embedded
class TaskDefinition extends Mistakable {

    String name
    String description
    String type // TODO: change to int
    // dependencies TODO
    ArrayList<Document> outputs // TODO: create a TaskOutput class


    TaskDefinition(Document doc) {
        this.name = doc.getString("name")
        this.description = doc.getString("description")
        this.type = doc.getString("type")
        outputs = doc.get("outputs") as ArrayList<Document>
    }

    boolean validate() {
        if (false in [Helper.valid(this.name), Helper.valid(this.description), Helper.valid(this.type)]) return false

        if (!this.outputs) return false

        def invalidOutput = this.outputs.find { output ->
            return (false in [Helper.valid(output.getString("name")), Helper.valid(output.getString("type")),
                              Helper.valid(output.getString("path"))])
        }

        return !invalidOutput
    }
}
