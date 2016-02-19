package br.ufscar.sead.loa.propeller

import br.ufscar.sead.loa.propeller.domain.ProcessDefinition
import br.ufscar.sead.loa.propeller.domain.ProcessInstance
import spock.lang.Specification

/**
 * Created by matheus on 2/19/16.
 * https://github.com/matheuss
 */
class ProcessInstanceSpec extends Specification {
    def setupSpec() {
        Propeller.instance.init([dbName: 'propeller', wipeDb: true])
        Propeller.instance.deploy(new File('test/resources/forca.json'), 1)
    }

    def cleanupSpec() {
        Propeller.instance.ds.DB.dropDatabase()
    }

    def "put some vars in a process instance"() {
        def instance

        setup:
        instance = Propeller.instance.instantiate('forca', 1) as ProcessInstance

        when:
        instance.putVariable('foo', 'bar', true)
        instance.putVariable('bar', 'foo', true)
        // ensure that the var is persisted
        instance = Propeller.instance.getProcessInstanceById(instance.id, 1)

        then:
        instance.getVariable('foo') == 'bar'
        instance.getVariable('bar') == 'foo'
        instance.vars.size() == 2

        cleanup:
        Propeller.instance.ds.delete(instance)
    }

    def "put a var in a process instance but 'forget' to save it"() {
        def instance

        setup:
        instance = Propeller.instance.instantiate('forca', 1) as ProcessInstance

        when:
        instance.putVariable('foo', 'bar', false)
        // ensure that the var is not persisted
        instance = Propeller.instance.getProcessInstanceById(instance.id, 1)

        then:
        instance.getVariable('foo') == null

        cleanup:
        Propeller.instance.ds.delete(instance)
    }
}
