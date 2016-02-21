package br.ufscar.sead.loa.propeller

import br.ufscar.sead.loa.propeller.domain.ProcessInstance
import br.ufscar.sead.loa.propeller.domain.TaskOutputInstance
import spock.lang.Specification

/**
 * Created by matheus on 2/19/16.
 * https://github.com/matheuss
 */
class TaskOutputInstanceSpec extends Specification {
    def "instantiate"() {
        def taskInstance
        def instance

        setup:
        Propeller.instance.init([dbName: 'propeller', wipeDb: true])
        Propeller.instance.deploy(new File('test/resources/forca.json'), 1)
        taskInstance = (Propeller.instance.instantiate('forca', 1) as ProcessInstance).pendingTasks.first()

        when:
        instance = new TaskOutputInstance(taskInstance.definition.outputs.first(), 'path')

        then:
        instance.definition == taskInstance.definition.outputs.first()

        cleanup:
        Propeller.instance.ds.DB.dropDatabase()
    }
}
