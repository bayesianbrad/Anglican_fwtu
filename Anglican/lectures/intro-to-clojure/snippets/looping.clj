(defn factorial 
  "computes n * (n-1) * ... * 1"
  [n]
  (if (= n 1)
    1
    (* n (factorial (- n 1)))))

(factorial 21)
; => ArithmeticException integer overflow  
;    clojure.lang.Numbers.throwIntOverflow (Numbers.java:1501)

(defn factorial [n]
  "computes n * (n-1) * ... * 1"
  (if (= n 1)
    1N
    (* n (factorial (- n 1)))))

(defn factorial 
  "computes n * (n-1) * ... * 1"
  [n]
  (if (= n 1)
    1
    (*' n (factorial (- n 1)))))


(let [[a b & others] stuff])

(factorial 10000)
; => StackOverflowError   clojure.lang.Numbers.equal (Numbers.java:216)

(defn factorial [n]
  "computes n * (n-1) * ... * 1"
  (loop [result 1
         ivals (range 2 (+ n 1))]
    (if (seq ivals)
      (recur (*' result (first ivals))
             (rest ivals))
      result)))

(factorial 10000)
; => 4023872600770937735437024339230039857193748642107146325437999104
;    2993851239862902059204420848696940480047998861019719605863166687
;    2994808558901323829669944590997424504087073759918823627727188732
;    5197795059509952761208749754624970436014182780946464962910563938
;    ...


(defn floop 
  "inner loop for factorial"
  [result ivals]
  (if (seq ivals)
    (floop (*' result (first ivals))
           (rest ivals))
    result))

(defn factorial [n]
  "computes n * (n-1) * ... * 1"
  (floop 1 
         (range 2 (+ n 1))))

(defn floop 
  "inner loop for factorial"
  [result ivals]
  (if (seq ivals)
    (recur (*' result (first ivals))
           (rest ivals))
    result))

(defn factorial [n]
  "computes n * (n-1) * ... * 1"
  (apply (fn floop [result ivals]
           (if (seq ivals)
             (recur (*' result (first ivals))
                    (rest ivals))
             result))
         [1 (range 2 (+ n 1))]))

(apply + [1 2])
; => 3

(defn factorial [n]
  "computes n * (n-1) * ... * 1"
  (reduce (fn [result i]
            (*' result i))
          1 (range 2 (+ n 1))))

(defn factorial [n]
  "computes n * (n-1) * ... * 1"
  (reduce *' 1 (range 2 (+ n 1))))


(defn factorial [n]
  "computes n * (n-1) * ... * 1"
  (reduce *' (range 1 (+ n 1))))

(defn cumsum [values]
  "returns a vector containing the 
  cumulative sum of values in a collection."
  (reduce (fn [output v]
            (conj output 
                  (+ (peek output) v)))
          [] values))
