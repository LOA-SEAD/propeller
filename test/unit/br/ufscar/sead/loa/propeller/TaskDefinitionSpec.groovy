package br.ufscar.sead.loa.propeller

import br.ufscar.sead.loa.propeller.domain.TaskDefinition
import org.bson.Document
import spock.lang.Specification

/**
 * Created by matheus on 2/19/16.
 * Last Modification by Lucas Suguinoshita on 8/29/16.
 * https://github.com/matheuss
 */
class TaskDefinitionSpec extends Specification {

    def setupSpec() {
        Propeller.instance.init([dbName: 'propeller', wipeDb: true])
    }

    def cleanupSpec() {
        Propeller.instance.ds.DB.dropDatabase()
    }

    def "create a task without any output"() {
        def definition

        setup:
        def doc
        doc = Document.parse(new File('test/resources/forca.json').text).get('tasks').first() as Document
        doc.remove('outputs')
        definition = new TaskDefinition(doc)

        expect:
        !definition.validate()
    }

    def "create a task without an invalid output"() {
        def definition

        setup:
        def doc
        doc = Document.parse(new File('test/resources/forca.json').text).get('tasks').first() as Document
        doc = Document.parse(doc.toJson().replace('perguntas.json', ''))
        definition = new TaskDefinition(doc)

        expect:
        !definition.validate()
    }

    def "create an optional task"() {
        def definition

        setup:
        def doc
        doc = Document.parse(new File('test/resources/forca.json').text).get('tasks').get(2) as Document
        definition = new TaskDefinition(doc)

        expect:
        definition.optional;
    }

    def "create a task with optional set to false as default"() {
        def definition

        setup:
        def doc
        doc = Document.parse(new File('test/resources/forca.json').text).get('tasks').first() as Document
        definition = new TaskDefinition(doc)

        expect:
        !definition.optional;
    }

    def "create a task with optional output"() {
        def definition

        setup:
        def doc
        doc = Document.parse(new File('test/resources/forca.json').text).get('tasks').get(3) as Document
        definition = new TaskDefinition(doc)

        expect:
        definition.optionalOutputs.size() == 1
        definition.optionalOutputs.get(0).name == "opcional.json"
    }

    def "create a task with output required by default"() {
        def definition

        setup:
        def doc
        doc = Document.parse(new File('test/resources/forca.json').text).get('tasks').first() as Document
        definition = new TaskDefinition(doc)

        expect:
        definition.outputs.size() == 1
        definition.optionalOutputs.size() == 0
        !definition.outputs.get(0).optional
    }

}