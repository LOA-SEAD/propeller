package br.ufscar.sead.loa.propeller.domain

import br.ufscar.sead.loa.propeller.util.Helper
import br.ufscar.sead.loa.propeller.util.Mistakable
import org.bson.Document
import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Embedded
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Field
import org.mongodb.morphia.annotations.Index
import org.mongodb.morphia.annotations.IndexOptions
import org.mongodb.morphia.annotations.Indexes
import org.mongodb.morphia.annotations.Id

/**
 * Created by matheus on 2/4/16.
 * https://github.com/matheuss
 */

@Entity('process_definition')
@Indexes(
        @Index(value = "uri", fields = @Field("uri"), options = @IndexOptions(unique = true))
)
class ProcessDefinition extends Mistakable {
    @Id
    ObjectId id
    String name
    String uri
    int version

    transient boolean deployed

    @Embedded
    ArrayList<TaskDefinition> tasks

    ProcessDefinition() {}

    ProcessDefinition(Document doc) {
        this.id = doc.getObjectId("_id")
        this.name = doc.getString("name")
        this.uri = doc.getString("uri")
        this.version = doc.getInteger("version")

        ArrayList<Document> tasks = doc.get("tasks") as ArrayList<Document>

        this.tasks = new ArrayList<>(tasks.size())

        tasks.each { task ->
            this.tasks.add(new TaskDefinition(task))
        }
    }

    boolean validate() {
        // version doesn't need to be checked. If it's null or isn't an int in the process.json,
        // an exception will be thrown.

        if (false in [Helper.valid(this.name), Helper.valid(this.uri)]) return false

        if (!this.tasks) return false

        def invalidTask = this.tasks.find { task ->
            // This Closure will return null if all tasks are valid; Otherwise, will return the first invalid task
            return !task.validate()
        }
        return !invalidTask
    }
}
