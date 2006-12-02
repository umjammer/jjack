/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   JJack
 * Version: 0.2
 *
 * Date:    2004-11-16
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file license.txt for details.
 *
 * Author:  Jens Gulden
 */


/**
 * Wrapper class for calling de.gulden.framework.jjack.JJack.main(args).
 * This class only redirects the invokation to the actual JJack main class,
 * which allows to invoke the program as "[java] [classapth] JJack".
 *  
 * @author  Jens Gulden
 * @version  0.2
 * @see  de.gulden.application.jjack.JJack
 */
public class JJack {

    // ------------------------------------------------------------------------
    // --- static method                                                    ---
    // ------------------------------------------------------------------------

    /**
     * Application entry method.
     * Redirects the invokation to the actual JJack main class de.gulden.application.jjack.JJack.
     *  
     * @param args command line parameters
     * @see  de.gulden.application.jjack.JJack#main
     */
    public static void main(String[] args) {
        de.gulden.application.jjack.JJack.main(args);
    }

} // end JJack
