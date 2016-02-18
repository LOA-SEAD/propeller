package br.ufscar.sead.loa.propeller

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
    }
}