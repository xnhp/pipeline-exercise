
## Overview

The task is to create a command-line application that

1. reads a file line-by-line
2. applies a sequence of operations to each line. This sequence is given by command-line arguments.
3. prints the results for each line (in order) to a file or to standard out.

## Architecture

 *(todo)*

## Features of this implementation
* A pipeline (sequence of operations) can go through any arbitrary intermediate types, in particular nontrivial types.
* The compiler can verify hardcoded pipelines for type compatibility
* For dynamic pipelines (as specified via the command-line), we can ensure type-safety at the time of construction of the pipeline.
* There can be multiple operations with the same keyword but a different type signature. The application will determine which operation is applicable. 
* The available standard operations can be overwritten.
* Additional operations can be added easily.
* Input is processed in chunks by a fixed-size thread pool.

## Terminology
* **Pipeline** -- In the abstract sense, a composite function representing a sequence of operations.
  * **Initial argument** -- In the `FnPipeline` implementation of the `Pipeline` interface, the pipeline is dependent on the value it will be applied to, i.e. for each input value we have a seperate pipeline object. This is because  • it follows lazy evaluation  • we use a runtime subtyping check to determine whether a candidate function can be attached to the pipeline.
* **Operation** -- A simple procedure that takes one input argument and returns a return value. These are mostly pure transformations.
  * **keyword** -- A string that identifies the operation with the corresponding command-line argument.
