package br.ufscar.sead.loa.propeller

import br.ufscar.sead.loa.propeller.domain.ProcessInstance
import br.ufscar.sead.loa.propeller.domain.TaskInstance
import spock.lang.Specification

/**
 * Created by matheus on 2/19/16.
 * https://github.com/matheuss
 */
class TaskInstanceSpec extends Specification {
    def setupSpec() {
        Propeller.instance.init([dbName: 'propeller', wipeDb: true])
        Propeller.instance.deploy(new File('test/resources/forca.json'), 1)
        Propeller.instance.instantiate('forca', 1)
    }

    def cleanupSpec() {
        Propeller.instance.ds.DB.dropDatabase()
    }

    def "put some vars in a task instance"() {
        def processInstance
        def instance

        setup:
        processInstance = Propeller.instance.getProcessesInstanceByOwner(1).first()
        instance = processInstance.pendingTasks.first() as TaskInstance

        when:
        instance.putVariable('foo', 'bar', true)
        instance.putVariable('bar', 'foo', true)
        // ensure that the var is persisted
        instance = Propeller.instance.getTaskInstance(instance.id as String, 1)

        then:
        instance.getVariable('foo') == 'bar'
        instance.getVariable('bar') == 'foo'
        instance.vars.size() == 2
    }

    def "put a var in a task instance but 'forget' to save it"() {
        def processInstance
        def instance

        setup:
        processInstance = Propeller.instance.getProcessesInstanceByOwner(1).first()
        instance = processInstance.pendingTasks.last() as TaskInstance

        when:
        instance.putVariable('foo', 'bar', false)
        // ensure that the var is not persisted
        instance = Propeller.instance.getTaskInstance(instance.id as String, 1)

        then:
        instance.getVariable('foo') == null
    }

    // Beware of changes made to the process/instance in the tests above
}
