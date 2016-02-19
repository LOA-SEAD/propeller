package br.ufscar.sead.loa.propeller

import br.ufscar.sead.loa.propeller.domain.ProcessDefinition
import org.bson.Document
import spock.lang.Specification

/**
 * Created by matheus on 2/19/16.
 * https://github.com/matheuss
 */
class ProcessDefinitionSpec extends Specification {

    def setupSpec() {
        Propeller.instance.init([dbName: 'propeller', wipeDb: true])
    }

    def cleanupSpec() {
        Propeller.instance.ds.DB.dropDatabase()
    }

    def "deploy a process without any task"() {
        def definition

        setup:
        def doc
        doc = Document.parse(new File('test/resources/forca.json').text)
        doc.remove('tasks')
        definition = new ProcessDefinition(doc, 1)

        expect:
        !definition.validate()
    }

    def "deploy a process with an invalid task"() {
        def definition

        setup:
        def doc
        doc = Document.parse(new File('test/resources/forca.json').text.replace('Banco de Questões', ''))
        definition = new ProcessDefinition(doc, 1)

        expect:
        !definition.validate()
    }
}
