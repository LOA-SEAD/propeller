package br.ufscar.sead.loa.propeller

import br.ufscar.sead.loa.propeller.domain.ProcessDefinition
import br.ufscar.sead.loa.propeller.domain.ProcessInstance
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
    }
 }