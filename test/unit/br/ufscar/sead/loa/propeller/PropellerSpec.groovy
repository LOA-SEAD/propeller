package br.ufscar.sead.loa.propeller

import br.ufscar.sead.loa.propeller.domain.ProcessDefinition
import br.ufscar.sead.loa.propeller.domain.ProcessInstance
import org.bson.types.ObjectId
import spock.lang.Specification

/**
 * Created by matheus on 2/18/16.
 * https://github.com/matheuss
 */
class PropellerSpec extends Specification {
    Propeller propeller = Propeller.instance.init([dbName: 'propeller', wipeDb: true])

    def "init"() {
        expect:
        propeller.options == [dbName: 'propeller', wipeDb: true]
        propeller.configured
        propeller.ds.DB.name == 'propeller'
    }

    def "init with null/missing dbName"() {
        setup:
        propeller.configured = false
        propeller.options = null
        propeller.ds = null

        when:
        propeller.init([wipeDb: false])

        then:
        thrown(MissingPropertyException)
        !propeller.configured
        propeller.options == null
        propeller.ds == null

        cleanup:
        Propeller.instance.init([dbName: 'propeller', wipeDb: false])
    }

    def "deploy a process"() {
        def p

        when:
        p = propeller.deploy(new File('test/resources/forca.json'), 1)

        then:
        p.name == 'Forca'
        p.deployed
        p.uri == 'forca'
        p.version == 1
        p.ownerId == 1

        cleanup:
        propeller.ds.delete(p)
    }

    def "instatiate a process"() {
        def definition
        def instance

        setup:
        definition = propeller.deploy(new File('test/resources/forca.json'), 1)

        when:
        instance = propeller.instantiate('forca', 1) as ProcessInstance

        then:
        instance.definition.uri == 'forca'
        instance.ownerId == 1
        instance.pendingTasks.size() == 2

        cleanup:
        propeller.ds.delete(definition)
        propeller.ds.delete(instance)
    }

    def "instantiate a process that doesn't exists"() {
        def instance

        when:
        instance = propeller.instantiate('foo', 1)

        then:
        instance == Propeller.Errors.PROCESS_NOT_FOUND
    }

    def "deploy an invalid process"() {
        def definition
        def instance

        setup:
        definition = new File('test/resources/forca.json').text
        definition = definition.replace('forca', '') // empty url

        when:
        instance = propeller.deploy(definition, 1)

        then:
        !instance.deployed
    }

    def "get a task instance by id"() {
        def pDefinition
        def pInstance
        def template
        def tInstance

        setup:
        pDefinition = propeller.deploy(new File('test/resources/forca.json'), 1)
        pInstance = propeller.instantiate(pDefinition.uri, 1) as ProcessInstance
        template = pInstance.pendingTasks.first()

        when:
        tInstance = propeller.getTaskInstance(template.id as String, 1)

        then:
        notThrown IllegalArgumentException
        tInstance != null
        tInstance.id == template.id

        cleanup:
        propeller.ds.delete(pDefinition)
        propeller.ds.delete(pInstance)
    }

    def "get a task instance with by an invalid id"() {
        when:
        propeller.getTaskInstance('invalid id', 1)

        then:
        thrown IllegalArgumentException
    }

    def "get a task instance with a valid but non existent id"() {
        expect:
        propeller.getTaskInstance(new ObjectId() as String, 1) == null
    }

    def "get a task instance that belongs to someone else"() {
        def pDefinition
        def pInstance

        setup:
        pDefinition = propeller.deploy(new File('test/resources/forca.json'), 1)
        pInstance = propeller.instantiate(pDefinition.uri, 1) as ProcessInstance

        expect:
        propeller.getTaskInstance(pInstance.pendingTasks.first().id as String, 2) == null

        cleanup:
        propeller.ds.delete(pDefinition)
        propeller.ds.delete(pInstance)
    }

    def "get all process instances by a given owner"() {
        def pDefinition
        def instance1
        def instance2
        def list

        setup:
        pDefinition = propeller.deploy(new File('test/resources/forca.json'), 1)
        instance1 = propeller.instantiate('forca', 1) as ProcessInstance
        instance2 = propeller.instantiate('forca', 1) as ProcessInstance

        when:
        list = propeller.getProcessInstancesByOwner(1)

        then:
        list.size() == 2
        list.first().id == instance1.id
        list.last().id == instance2.id

        cleanup:
        propeller.ds.delete(pDefinition)
        propeller.ds.delete(instance1)
        propeller.ds.delete(instance2)
    }

    def "get all process instances by an owner that doesn't have any instance"() {
        expect:
        propeller.getProcessInstancesByOwner(1).empty
    }

    def "get a process instance by id"() {
        def instance

        setup:
        propeller.deploy(new File('test/resources/forca.json'), 1)
        instance = propeller.instantiate('forca', 1) as ProcessInstance

        expect:
        propeller.getProcessInstanceById(instance.id.toString(), 1).id == instance.id
    }

    def "get a process instance by id that does not exists"() {
        expect:
        propeller.getProcessInstanceById(new ObjectId().toString(), 1) == null
    }

    def "get a process instance by id that belongs to someone else"() {
        def instance

        setup:
        propeller.deploy(new File('test/resources/forca.json'), 1)
        instance = propeller.instantiate('forca', 1) as ProcessInstance

        expect:
        propeller.getProcessInstanceById(instance.id.toString(), 2) == null
    }

    def "get a process instance by id in 'admin' mode"() {
        def instance

        setup:
        propeller.deploy(new File('test/resources/forca.json'), 1)
        instance = propeller.instantiate('forca', 1) as ProcessInstance

        expect:
        propeller.getProcessInstanceById(instance.id.toString(), 0).id == instance.id
    }
}