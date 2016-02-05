package br.ufscar.sead.loa.propeller.util

/**
 * Abstract class to indicate that an object can contain errors
 * Created by matheus on 2/4/16.
 * https://github.com/matheuss
 */
abstract class Mistakable {

    /**
     *
     * @return true if the object is valid
     */
    abstract boolean validate()
}
