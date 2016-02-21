package br.ufscar.sead.loa.propeller.domain

import org.mongodb.morphia.annotations.Embedded

/**
 * Created by matheus on 2/8/16.
 * https://github.com/matheuss
 */
@Embedded
class TaskOutputInstance {
    TaskOutputDefinition definition
    String path

    TaskOutputInstance() {}

    TaskOutputInstance(TaskOutputDefinition definition, String path) {
        this.definition = definition
        this.path = path
    }
}
