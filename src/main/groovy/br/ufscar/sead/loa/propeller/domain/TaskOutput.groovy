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
class TaskOutput extends Mistakable{
    String name
    String type
    String path

    TaskOutput() {}

    TaskOutput(Document doc) {
        this.name = doc.getString('name')
        this.type = doc.getString('type')
        this.path = doc.getString('path')
    }

    @Override
    boolean validate() {
        return !(false in [Helper.valid(this.name), Helper.valid(this.type), Helper.valid(this.path)])
    }
}
