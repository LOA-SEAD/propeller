package br.ufscar.sead.loa.propeller

import br.ufscar.sead.loa.propeller.util.Helper
import spock.lang.Specification

/**
 * Created by matheus on 2/19/16.
 * https://github.com/matheuss
 */
class HelperSpec extends Specification{

    def "try to validate an enpty string"() {
        expect:
        !Helper.valid("")
    }

    def "try to validate a null string"() {
        expect:
        !Helper.valid(null)
    }

    def "try to validate a string"() {
        expect:
        Helper.valid("propeller")
    }

    def "try to validate a string by it's length"() {
        expect:
        !Helper.valid("", 0)
        !Helper.valid("", 1)
        Helper.valid("1", 1)
        !Helper.valid("1", 2)
    }
}
