package br.ufscar.sead.loa.propeller.util

/**
 * Created by matheus on 2/5/16.
 * https://github.com/matheuss
 */
class Helper {

    static boolean valid(String str) {
        return str != null && str != ""
    }

    static boolean valid(String str, int minLength) {
        return valid(str) && str.length() >= minLength
    }

    // TODO: "valid" probably isn't a good name. Find a better one.
}
