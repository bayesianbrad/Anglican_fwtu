(ns anglican.importance
  "Importance sampling"
  (:refer-clojure :exclude [rand rand-int rand-nth])
  (:use anglican.state
        anglican.inference))

(derive ::algorithm :anglican.inference/algorithm)

(defmulti checkpoint
 (fn [alg cpt] [alg (type cpt)]))

(defmethod checkpoint 
  [::algorithm anglican.trap.observe] [_ obs]
  (let [cont (:cont obs)
        lp (observe* (:dist obs) (:value obs))
        state (:state obs)])
  #(cont nil (add-log-weight state lp)))

(defmethod checkpoint 
  [::algorithm anglican.trap.sample] [_ smp]
  (let [cont (:cont smp)
        x (sample* (:dist smp))
        state (:state smp)]
    (fn [] (cont x state))))

(defmulti infer
  (fn [alg prog value & _] alg))

(defmethod infer :importance 
  [alg prog value & {}]
  (letfn [(sample-seq []
            (let [result (exec ::algorithm 
                               prog 
                               value 
                               initial-state)]
              (lazy-seq
                (cons (:state result)
                      (sample-seq)))))]
    (sample-seq)))
