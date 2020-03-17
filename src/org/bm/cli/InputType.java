package org.bm.cli;

/**
 * Note that these values are case-insensitive mapping targets for CLI options, using
 * an option of `CommandLine`.
 *
 * If argument strings that can not be expressed as enum constants are required, we'd need
 * a separate data structure or procedure for mapping.
 *
 * @author Benjamin Moser
 */
public enum InputType {
    STRING, INT, DOUBLE
}
