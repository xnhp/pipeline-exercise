
## Overview

The task is to create a command-line application that

1. reads a file line-by-line
2. applies a sequence of operations to each line. This sequence is given by command-line arguments.
3. prints the results for each line (in order) to a file or to standard out.

## Architecture

The basic flow is as follows:

1. Parse arguments from command-line
2. Register operations
3. Read input file, divide the input stream in chunks
4. Process each chunk, potentially multithreaded
5. Return the result to a consumer that prints either to StdOut or to a file

### Features of this implementation
* **A pipeline (sequence of operations) can go through any arbitrary intermediate types**, in particular nontrivial types.
* The compiler can verify hardcoded pipelines for type compatibility
* For dynamic pipelines (as specified via the command-line), we can ensure type-safety at the time of construction of the pipeline.
* There can be multiple operations with the same keyword but a different type signature. The application will determine which operation is applicable. 
* The available standard operations can be overwritten.
* Additional operations can be added easily.
* Input is processed in chunks by a fixed-size thread pool.
 
### Why do we have seperate functions for .e.g `negateInt` and `negateDouble`?
 
 why seperate functions for negate for int and double?
 
 Alternative approaches:
 
 - **Use common supertype** — the *lub* for `Integer` and `Double` is `Number`, which does not support operations like `(n) -> -n` or `(n)->n*(-1)` generically.
 
     So, we would have to make an `instanceof` check in the function body
 
         public final static Function<Number, Number> negate = (Number n) -> {
                 if (n instanceof Integer) return ((Integer) n) * (-1);
                 else if (n instanceof Double) return ((Double) n) * (-1);
                 else {
                     // ???
                 }
              };
 
     This has several disadvantages
 
     - What do we do if the function receives an instance of the parent class `Number`? — We have to cover all possible subtypes of `Number` or come up with reasonable error behaviour.
     - We make it harder for a contributor to write code that adds support for additional subtypes of `Number` without overwriting already implemented cases. — Or, to write code that overrides just one implementation.
     - If `n` is instance of a *subclass* of Integer, we cast upwards. — Of course we could check if `n` is a direct instance of of Integer but this would require putting down even more constraints and checking more cases. — We could (probably) use reflection to determine the actual instance class of `n` and then cast into that but that is even more implementation effort.
 - **Explicitly declare accepted in-types in list form** i.e. accepts = [Integer, Double].
     - We still have to make `instanceof` checks
     - We still have to cast, with the disadvantages described above.
 - *Probably the best way to handle this would be to have an interface/trait/typeclass "negatable".But we don't. Also disadvantage: cant wrap every data type I want to handle. Doesnt solve the root problem.*

## Disadvantages / Missing
- When determining operation compatiblity (check whether an operation can be attached to the pipeline), 
  we currently only check for *exact* type matches. In particular, subtyping is not considered (we are able to handle
  parameterised types though).
- stacktraces might be hard to follow because of heavy use of anonymous functions and method chaining
- tests could be better / more precise (some tests use more than should be tested)
- missing: ability to add new input types
- output to file could be optimised by using a buffered writer

## Terminology
* **Pipeline** -- In the abstract sense, a composite function representing a sequence of operations.
* **Operation** -- A simple procedure that takes one input argument and returns a return value. These are mostly pure transformations.
  * **keyword** -- A string that identifies the operation with the corresponding command-line argument.
