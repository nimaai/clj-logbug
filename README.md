# logbug - Cross-cutting utils for logging and debugging in Clojure


## Usage

### Debug Wrappers

There are two wrapping functionalities:
1. wrap calls to log invocation and results,
2. wrap calls to remember (and later possibly re-apply) the last arguments.

#### Wrap Functions for Debug Logging

This functionality logs invocation and result of a function referenced by
a var.

Example enable:

    (debug/wrap-with-log-debug #'some-var)

Example log output:

    [DEBUG some-ns clojure-agent-send-off-pool-5] [some-var "invoked" {:args ( ...
    ...
    [DEBUG some-ns clojure-agent-send-off-pool-5] [some-var "returns" {:res ...

Example disable again:

    (debug/unwrap-with-log-debug #'some-var)


#### Wrap Functions for Reapplying last Arguments


    (debug/wrap-with-remember-last-argument #'some-var)

    (debug/get-last-argument #'some-var)

    (debug/re-apply-last-argument #'some-var)


#### Wrap a Complete Namespace

Example require the debug namespace:

    (require ['drtom.logbug.debug :as 'debug])

Example wrap all vars in the current namespace:

    (debug/debug-ns *ns*)

Example disable wrappers in the current namespace:

    (debug/undebug-ns *ns*)

#### Notes

The use of `require` will "remove" existing wrappers. The last arguments are
stored within the `debug` ns and thus they survive `require` etc.


## License

Copyright © 2015 Thomas Schank

Distributed under the APGL.
