(let [expr (read-string "(+ 1 2)")]
  (prn expr) ; => (+ 1 2)
  (prn (class expr)) ; => clojure.lang.Persistentlist
  (prn (class (first expr))) ; => clojure.lang.Symbol
  (eval expr)) ; => 6
  

(let [expr '(+ 1 2)]
  (prn expr) ; => (+ 1 2)
  (prn (class expr)) ; => clojure.lang.Persistentlist
  (prn (class (first expr))) ; => clojure.lang.Symbol
  (eval expr)) ; => 6
  