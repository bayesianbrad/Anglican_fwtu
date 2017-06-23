;; gorilla-repl.fileformat = 1

;; **
;;; # Bonus Exercise 1: Captcha
;; **

;; @@
(ns captcha
  (:refer-clojure :exclude [rand rand-nth rand-int name read])
  (:require [gorilla-plot.core :as plot]
            [anglican.rmh :as rmh]
            [anglican.smc :as smc]
            [anglican.ipmcmc :as ipmcmc]
            [clojure.core.matrix :as m]
            [gorilla-repl.image :as image]
            [clojure.java.io :as io])
  (:use [anglican runtime emit core inference]
        [exercises captcha]
        :reload)
  (:import [javax.imageio ImageIO]
           [java.io File]
           [robots.OxCaptcha OxCaptcha]))

(def ...complete-this... nil)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;captcha/...complete-this...</span>","value":"#'captcha/...complete-this..."}
;; <=

;; **
;;; ## Captcha renderer
;;; 
;;; We will try to break some captchas by doing inference over letter identities in a generative model containing both letter identities and the captcha image (2D matrix of 0-255 numbers). Let's explore the renderer...
;;; 
;;; Remember that the image is 50 pixels high and 150 pixels wide. x-axis goes from left to right and y-axis goes from top to bottom.
;;; 
;;; Play around with the parameters to get a feel for the renderer...
;; **

;; @@
(def xs [1 20 40])
(def ys [30 30 30])
(def letters "abc")
(def salt-and-pepper true)
(def render-mode OxCaptcha/ABSOLUTE) ; either OxCaptcha/ABSOLUTE or OxCaptcha/RELATIVE
(def filename "tmp/captcha/test.png")
(render-to-file xs ys letters salt-and-pepper filename :mode render-mode)
(image/image-view (ImageIO/read (File. filename)) :type "png" :alt "captcha")
;; @@
;; =>
;;; {"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAM0ElEQVR42u3ch49VVRcF8PkjMDEhtgioEVsU7Ap2xN67qChixS4iFmzYGzbsFTt2xV5Asfcu9gr2hgXcfr+dnMmIA5+DzDCM5yQn7/HuveeeeXvdtdde5zwaorY5vjU0NLS7e8zyGf3000/xxhtvxG233RYnn3xyHH300TFixIh44IEH4oMPPojff/+93X+Jtc0CIM7qAX/44Yd44YUX4oorrogDDjggBgwYEEcccUSMHj063nrrrfjtt9/axZNaATqHAevbb7+NcePGxamnnhqbbrpprL766rHTTjvFRRddFC+99FL8+uuvNQVVYLX8CwSsRx99NE444YTo06dPLL/88rHVVlvFeeedl0zWkYBVWxszVgVWLQ7+0RV//PFHTJ06NYU3jeRV95ljLQVWGa9pn3aclsxrypQpjXPyfmbHq60VGUtABGfy5Mnx3XffxRdffJHVHOGt0Un6a6+9Fu+++258/vnn8f333ydgBHR6wFIZjh8/PiZOnBiffvppvPPOO1k9vvnmmznOZ599ltf+8ssvef8ZgQm4f/zxxxzr/fffz3FeeeWV7N77zDHnOLe9g6wjarW//EWAAVCTJk2Kt99+O5588sm4995746abboqrrroqLr744rjggguyX3LJJXH99dfnce3DDz9MgH355Zd/A9Zmm20WJ554Ytx6663x+OOPx1133RWjRo2Kyy+/PKvH6667Lu6555545plnEhQANi0gvMdIqs5PPvkkwc3CuPHGG3OcCy+8MLv35vvggw/Gyy+/nCAGsBmBtbZWBBZQCcBHH32UgBKwM888M62C/fffP/bYY4/o379/9OvXL3bYYYfYeeed80lzXEDvv//+ZDXs9tBDDzUCq0ePHrH22mvHPvvsk+Ay5tChQ2OvvfaK3XbbLfvee++dn5177rlx5513JmgwDvYCqMJSX3/9dTKSewH5sGHDYt99941dd90156X6NN5+++0Xxx57bFx22WXx8MMPx4QJE5J9K7hmA7B+/vnnZB1sM3LkyDj44INjxx13jC222CI233zzTGfeb7LJJtG3b99YY4010krwfpdddolTTjklWee5556LMWPGxPHHH5/AWnzxxWPppZeO9ddfP8fja2277bY5JibbaKONYr311stXwDjq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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x7db8aa6 \"BufferedImage@7db8aa6: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"}
;; <=

;; **
;;; You can also check the images in the folder `tmp/captcha`. We can also get the actual matrix of values:
;; **

;; @@
(def test-captcha (render xs ys letters salt-and-pepper :mode render-mode))
(m/shape test-captcha)
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-unkown'>50</span>","value":"50"},{"type":"html","content":"<span class='clj-unkown'>150</span>","value":"150"}],"value":"[50 150]"}
;; <=

;; **
;;; ## Approximate Bayesian computation (ABC) likelihood
;;; 
;;; Our generative model will have the form of
;;; 
;;; \begin{align}
;;; 	\theta = (\text{x-offsets}, \text{y-offsets}, \text{letters}, \text{other-params}) &\sim p(\theta) \\\\
;;;     y = \text{captcha-image} &\sim p(y | \theta).
;;; \end{align}
;;; 
;;; Using the backend inference algorithms, we would like to find the posterior @@p(\theta | y)@@.
;;; 
;;; In order to do this, we need to specify the prior @@p(\theta)@@ and the likelihood @@p(y | \theta)@@ in our probabilistic program. We want the likelihood to be a distribution which is high when the rendered captcha render(@@\theta@@) is similar to the observed one @@y@@ and low otherwise. This will force the posterior to concentrate around the correct letters positions and identities that might have generated the observed captcha. This way of designing likelihoods to encode similarity is called approximate Bayesian computation (ABC) and the likelihood function is called an ABC likelihood.
;;; 
;;; Recalling that render(@@\theta@@) and @@y@@ are both @@50 \times 150@@ integer matrices, we can for example design an ABC likelihood as follows:
;;; \begin{align}
;;; 	p(y | \theta) = \text{Normal}(\text{flatten}(y); \text{flatten}(\text{render}(\theta)), \sigma^2 I)
;;; \end{align}
;;; where `flatten` flattens matrices into a vector and @@\sigma@@ is a parameter designed by us.
;;; 
;;; In this example, we will use a very similar ABC likelihood:
;;; \begin{align}
;;; 	p(y | \theta) = \text{Normal}(\text{reduce-dim}(\text{flatten}(y)); \text{reduce-dim}(\text{flatten}(\text{render}(\theta))), \sigma^2 I).
;;; \end{align}
;;; The difference is that we use a function `reduce-dim` which additionally reduces the dimension of the flattened, @@7500@@-dimensional vector, to a @@500@@-dimensional one through a linear projection by a random projection matrix @@R \in \mathbb R^{500 \times 7500}@@ (see section 2.1 in [here](http://www.ime.unicamp.br/~wanderson/Artigos/randon_projection_kdd.pdf)). The reason for this is to make the posterior space "smoother" so that it is easier for our hill-climbing based algorithms to arrive at the right answer.
;;; 
;;; The ABC likelihood which takes two @@50 \times 150@@ integer matrices has been implemented for you. You can try to generate some captchas and calculate the (log-)likelihood.
;; **

;; @@
;; First captcha
(def xs-1 [1 20 40]) ;...complete-me...)
(def ys-1 [30 30 30]) ;...complete-me...)
(def letters-1 "abc") ;...complete-me...)
(def salt-and-pepper false)
(def test-captcha-1 (render xs-1 ys-1 letters-1 salt-and-pepper :mode render-mode))
(def filename-1 "tmp/captcha/test-1.png")
(render-to-file xs-1 ys-1 letters-1 salt-and-pepper filename-1 :mode render-mode)

;; Second captcha
(def xs-2 [1 20 40]) ;...complete-me...)
(def ys-2 [30 30 30]) ;...complete-me...)
(def letters-2 "bcd") ;...complete-me...)
(def salt-and-pepper false)
(def test-captcha-2 (render xs-2 ys-2 letters-2 salt-and-pepper :mode render-mode))
(def filename-2 "tmp/captcha/test-2.png")
(render-to-file xs-2 ys-2 letters-2 salt-and-pepper filename-2 :mode render-mode)

;; View the two captchas
[(image/image-view (ImageIO/read (File. filename-1)) :type "png" :alt "captcha-1")
 (image/image-view (ImageIO/read (File. filename-2)) :type "png" :alt "captcha-2")]

;; Inspect the log-likelihood value of the ABC likelihood described above for different abc-sigma's
(def abc-sigma 1) ;...complete-me...) ; Standard deviation calculated from each pixel (pixels range from 0 to 255)
(observe* (abc-dist test-captcha-1 abc-sigma) test-captcha-2)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-double'>-562.8459442023368</span>","value":"-562.8459442023368"}
;; <=

;; **
;;; What are the typical values of log-likelihood?
;;; How does the choice of @@\sigma@@ (`abc-sigma`) affect the variability of the log-likelihood? Why?
;;; 
;;; The choice of @@\sigma@@ needs to be calibrated in such a way that the variability of the log-likelihood is just right. If it is too low then the posterior space will not be peaked enough for inference to zoom in onto the right answer (a valid sample from a posterior might not be the right answer). If it is too high then the posterior space will be too peaked for inference to move from one posterior mode (e.g. wrong one) to another (e.g. right one).
;; **

;; **
;;; ## The captcha solving probabilistic program
;;; 
;;; Fill in the necessary blanks in the program in order to form a generative model. You can use the following fixed values to make sure the prior doesn't generate letters outside the captcha image:
;; **

;; @@
WIDTH ; width of the captcha image
HEIGHT ; height of the captcha image
avg-width ; average width of a letter
avg-height ; average height of a letter
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-long'>25</span>","value":"25"}
;; <=

;; @@
;; Model specific
(def abc-sigma 1) ;...complete-me...) ; Standard deviation calculated from each pixel (pixels range from 0 to 255)
(def letter-dict "abcdeghk") ; Captcha letter dictionary (keep it reasonably small for good inference)

(with-primitive-procedures [render abc-dist overlap-abc-dist index-of-sorted retain-visible]
  (defquery captcha [baseline-image letter-dict abc-sigma]
    (let [;; prior for number of letters
          num-letters (sample (uniform-discrete 3 6))

          ;; prior for the letter positions and identities
          [xs ys letter-ids visible?] (loop [xs [] ys [] letter-ids [] visible? []]
                                        (if (= (count xs) num-letters)
                                          [xs ys letter-ids visible?]
                                          (let [x (round (sample (uniform-continuous 0 (- WIDTH avg-width)))) 
                                                y (round (sample (uniform-continuous avg-height HEIGHT)))
                                                letter-id (sample (uniform-discrete 0 (count letter-dict)))
                                                v (sample (flip 0.5))]
                                            (recur (conj xs x)
                                                   (conj ys y)
                                                   (conj letter-ids letter-id)
                                                   (conj visible? v)))))

          ;; Reorder xs, ys, letter-ids according to xs
          indices (index-of-sorted xs)

          ;; Take only visible
          indices (retain-visible indices visible?)

          xs (map (partial nth xs) indices)
          ys (map (partial nth ys) indices)
          letter-ids (map (partial nth letter-ids) indices)

          letters (apply str (map (partial nth letter-dict) letter-ids))

          ;; Render image using renderer from ...
          rendered-image (render xs ys letters false :mode OxCaptcha/ABSOLUTE)]

      ;; ABC-style observe
      (observe (abc-dist rendered-image abc-sigma) baseline-image)
      (observe (overlap-abc-dist avg-width 10000) xs)
      {:xs xs
       :ys ys
       :letters letters
       :rendered-image rendered-image})))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;captcha/captcha</span>","value":"#'captcha/captcha"}
;; <=

;; **
;;; ## Generate some observes on which we want to do inference
;; **

;; @@
(def num-captchas 10)
(def tmp (generate-test-samples letter-dict num-captchas "tmp/captcha/"))

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;captcha/tmp</span>","value":"#'captcha/tmp"}
;; <=

;; @@
(def num-captchas 10)
(def tmp (generate-test-samples letter-dict num-captchas "tmp/captcha/"))
(def letters (:letters tmp))
(def observes (:observes tmp))
(map #(image/image-view (ImageIO/read (File. (str "tmp/captcha/captcha-" % "-ground.png")))
                          :type "png" :alt "captcha")
     (range 1 (inc num-captchas)))
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAAAyCAYAAAC+jCIaAAAFUklEQVR42u2cTyg9XxTA38bWkhJlQxYo/5V/CfkTJQlFIQpJSWKjlIWFIpGSSGIhJSuRZCELyUKSJHtLGwu78+1za17vN795f2a+T735vnPq9qZ775w5nM+cc+feOxMQFZVfkID+C1QULBUFS0XBUlFRsFQULBUFK7Hl5+dHvr6+5PPzUz4+PkzhmDra1FYFy5PglPf3d7m7u5PT01NTOKaONrVVwfIk3PG3t7eys7Mj8/PzpnBMHW1qq4LlSUgn3Pk4qbm52RSOqaNNbVWwPMnb25vs7+/L4OCg5ObmmsIxdbSprQqWgqVgqbMULAVLbVWw4uus5+fn/8wb0ZcSj/kj+5xULLrd2vqvznP5Fqy+vj5ZX1+Xi4sLub+/l/Pzczk+Pjb9KBxTRxvOc+M4CyjOc6vbra1/Y6eCFWewsrOzzWP8zMyMrKysyNramnmkn5iYMH0oHFNH29nZmYkWRIZoTqOdfvTnPLe63drq1U4F6xfASk9Pl6KiImlvb5eBgQEZGRmR7u5u6ezsNE6sq6uTmpoaqa+vN/ULCwtycnISTEXRoCJ6HB4eGocTbdra2oJzUlahjjb60JdzLCDd2BpJl5/h8iVYqampJhKUlpZKS0uL9PT0GMfgOI4bGxulpKREcnJyTH+cB1xEBNKNk8NCodre3pbJyUlpamqSiooKqaqqktbWVqObwjF1tNFnenraAAFUDw8P/7M1IyND8vPzpba2Vjo6OqSrqytoq10X1+X6fofLl2ClpKRIWlqaFBQUGEdNTU3J8vKybG1tmbK4uGgcV15eLpmZmeYcogTpBoc5RS3qAANAcG51dbXRzy+60GnXb/UBiLm5OQMu4yjACLXVuhGIpMPDwxF18cv1LVD9ur7oW7BwFA6dnZ01TmAt7unpyZTLy0tZXV2V/v5+4yzgIirgfAbKTmt21F1dXRlHo5doV1xcbHSgC51O+ulTWFgYBPfg4MAM1C1bA4GAAQs7YtHFdbk+dmCPX9cXfZsKy8rKZHx83Djy5eUl+DRlpTQchnMaGhokKyvLOJ/+PIXZ1+w45/X1Vfb29mRoaMj05TpEQyLhzc1NMC1Z+qkDCKINqZZfwNrd3TX19psgmi7a6MM5XB87sAe7/JgOfTt4Z2yCM4hU9nSBI4gGpBrGMTg20kQl56MHfeilL+CSYhn028dlHFMHvBsbG2bQDcRHR0emhIJl3QTRdNFGH/pyXqS/T8H6xekGgAEcAHK6o93MgBM1SJGkSlJmLI4Nt+8KPaGpMNpNEA7saKlbwfrlmfdwyyRuziFikCJJlVYajAZuuJ2iPBWGDt5juQnsEdZKh+FSt4LlE7Diub7nVde/tsaoYClYClYigRVrKlSwFCxX46JYB+86xtLBe3DwzrxXtOmMWKcb9KkwicGyTzcwUx9p7gkQWBoiwrCoHGmCVOexkhgsCxTAYGnG6h9utjwUBECMtKSjM+9JDJYFC4vIRC0WiyOtFdJvaWnJgAAEwGVFJKdFaBbMWRAfHR2Vzc1NXStMFrCsqBXr7gZrnxZAVVZWmnbqiXrhts0AIHuy0K27G5IILLf7sdjzRSHSsDOUyEPUc9rol5eXZ4ABrN7eXt2PlSji5e1iL+e42UHKmGpsbCyYJq1vM9ivyy5WAOKJk3QJOLqDNEHEy/cQvH5DIdY976QwngAZeIe+BOF0XeAjijGwt1Kp7nlPAPHyBZe/+epLLG/pMLB2gsDputfX1/L4+GgG6/qWTgKJl29OxeM7VV7eK4x03e/vb32vUEVFwVJRsFQULBUVBUtFwVJRsFRUFCwVBUtFwVJR8SB/AMo4btCY+s75AAAAAElFTkSuQmCC\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x17cd2af0 \"BufferedImage@17cd2af0: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0xc94ca80 \"BufferedImage@c94ca80: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x5512ac61 \"BufferedImage@5512ac61: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x1841d025 \"BufferedImage@1841d025: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x66a5e377 \"BufferedImage@66a5e377: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x4c4fe5e9 \"BufferedImage@4c4fe5e9: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0xc7e39a5 \"BufferedImage@c7e39a5: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x1aa800f7 \"BufferedImage@1aa800f7: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x535fe68e \"BufferedImage@535fe68e: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"},{"type":"html","content":"<img src=\"data:image/png;base64,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\" width=\"150\" height=\"50\" alt=\"captcha\" />","value":"#object[java.awt.image.BufferedImage 0x600c21a1 \"BufferedImage@600c21a1: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"]"}],"value":"(#gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x17cd2af0 \"BufferedImage@17cd2af0: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0xc94ca80 \"BufferedImage@c94ca80: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x5512ac61 \"BufferedImage@5512ac61: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x1841d025 \"BufferedImage@1841d025: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x66a5e377 \"BufferedImage@66a5e377: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x4c4fe5e9 \"BufferedImage@4c4fe5e9: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0xc7e39a5 \"BufferedImage@c7e39a5: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x1aa800f7 \"BufferedImage@1aa800f7: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x535fe68e \"BufferedImage@535fe68e: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50} #gorilla_repl.image.ImageView{:image #object[java.awt.image.BufferedImage 0x600c21a1 \"BufferedImage@600c21a1: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@3d58bff4 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 150 height = 50 #numDataElements 1 dataOff[0] = 0\"], :alt \"captcha\", :type \"png\", :width 150, :height 50})"}
;; <=

;; **
;;; ## Inference
;; **

;; **
;;; Sequential Monte Carlo:
;;; 
;;; We put SMC here only for illustrative purposes and to get you to think about the differences between an SMC and an MCMC (below) scheme. An MCMC scheme is much better suited to this probabilistic program.
;; **

;; @@
(defn- smc-captcha-posterior-states-l [query num-particles value]
  (take num-particles (infer :ipmcmc
                             query
                             value :number-of-particles 500 :number-of-nodes 8)))

;(defn- smc-captcha-posterior-states-l [query num-particles value]
;  (take num-particles (infer :smc
;                             query
;                             value :number-of-particles num-particles)))

(defn smc-captcha-MAP-state-l [query num-particles value]
  (let [states (smc-captcha-posterior-states-l query num-particles value)
        log-weights (map :log-weight states)]
    (nth states (max-index log-weights))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;captcha/smc-captcha-MAP-state-l</span>","value":"#'captcha/smc-captcha-MAP-state-l"}
;; <=

;; @@
;; Don't run with too many particles (up to 1000) as it doesn't work even with 10000 particles and can cause memory issues.
(def num-particles 100)
(def inferred-captchas-smc 
  (time 
    (doall (map extract-from-state
                (map #(smc-captcha-MAP-state captcha num-particles [% letter-dict abc-sigma])
                     observes)
                (map #(str "tmp/captcha/captcha-" % "-smc.png") (range 1 (inc (count observes))))))))
;; @@
;; ->
;;; &quot;Elapsed time: 13012.152 msecs&quot;
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;captcha/inferred-captchas-smc</span>","value":"#'captcha/inferred-captchas-smc"}
;; <=

;; **
;;; Random-walk Metropolis Hastings (a Markov Chain Monte Carlo scheme):
;; **

;; @@
;; Start with small values to see what it does but later use 10000 for good performance (can take around 10 minutes...)
(def num-iters 100)
(def inferred-captchas-rmh 
  (time 
    (doall (map extract-from-state
                (map #(rmh-captcha-posterior-state captcha num-iters [% letter-dict abc-sigma])
                     observes)
                (map #(str "tmp/captcha/captcha-" % "-rmh.png") (range 1 (inc (count observes))))))))
;; @@
;; ->
;;; &quot;Elapsed time: 13315.759 msecs&quot;
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;captcha/num-iters</span>","value":"#'captcha/num-iters"}
;; <=

;; **
;;; Letter identities and recognition rates:
;; **

;; @@
(def smc-letters (map :letters inferred-captchas-smc))
(def rmh-letters (map :letters inferred-captchas-rmh))
(def smc-rate (* 100.0 (/ (count (filter identity (map = letters smc-letters))) (count letters))))
(def rmh-rate (* 100.0 (/ (count (filter identity (map = letters rmh-letters))) (count letters))))

"--- Ground truth ---"
(map #(image/image-view (ImageIO/read (File. (str "tmp/captcha/captcha-" % "-ground.png")))
                          :type "png" :alt "captcha")
     (range 1 (inc num-captchas)))
letters

"-------- SMC -------"
(map #(image/image-view (ImageIO/read (File. (str "tmp/captcha/captcha-" % "-smc.png")))
                          :type "png" :alt "captcha")
     (range 1 (inc num-captchas)))
smc-letters
(str "SMC: recognition rate: " smc-rate "%")


"-------- RMH -------"
(map #(image/image-view (ImageIO/read (File. (str "tmp/captcha/captcha-" % "-rmh.png")))
                          :type "png" :alt "captcha")
     (range 1 (inc num-captchas)))
rmh-letters
(str "RMH: recognition rate: " rmh-rate "%")
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-string'>&quot;RMH: recognition rate: 70.0%&quot;</span>","value":"\"RMH: recognition rate: 70.0%\""}
;; <=

;; **
;;; Which algorithm works better? Why?
;; **
