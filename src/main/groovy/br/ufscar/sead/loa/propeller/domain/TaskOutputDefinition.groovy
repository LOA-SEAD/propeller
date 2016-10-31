package br.ufscar.sead.loa.propeller.domain

import br.ufscar.sead.loa.propeller.util.Helper
import br.ufscar.sead.loa.propeller.util.Mistakable
import org.bson.Document
import org.mongodb.morphia.annotations.Embedded

/**
 * Created by matheus on 2/8/16.
 * https://github.com/matheuss
 */
@Embedded
class TaskOutputDefinition extends Mistakable {
    String name
    String type
    String path
    boolean optional
    boolean shareable

    TaskOutputDefinition() {}

    TaskOutputDefinition(Document doc) {
        this.name = doc.getString('name')
        this.type = doc.getString('type')
        this.path = doc.getString('path')
        this.optional = doc.getBoolean('optional', false)
        this.shareable = doc.getBoolean('shareable', true)
    }

    @Override
    boolean validate() {
        return !(false in [Helper.valid(this.name), Helper.valid(this.type)])
    }
}
