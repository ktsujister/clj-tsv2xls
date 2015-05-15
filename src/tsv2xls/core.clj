(ns tsv2xls.core
  (:require [clojure.java.io :as io]
            [clojure.stacktrace :refer [print-stack-trace]]
            [clojure.tools.cli :as cli])
  (:import (org.apache.poi.hssf.usermodel HSSFWorkbook)
           (org.apache.poi.xssf.streaming SXSSFWorkbook))
  (:gen-class))

(defn- get-output-file
  "get output filename from options."
  [{:keys [files outfile format] :as options}]
  {:pre [(seq format) (or files outfile)]}
  (if (seq outfile)
    outfile
    (clojure.string/replace (first files) #"(.+)\.(?:txt|tsv)" (str "$1." format))))

(def max-sheet-name-length 31)

(defn- drop-common-strings [strings]
  (let [drop-count
        (->> (apply map vector strings)
             (take-while #(apply = %))
             count)]
    (map #(apply str (drop drop-count %)) strings)))

(defn- get-sheet-names [files]
  "get sheetnames from input files. handle duplicate names"
  (let [base-names (map #(clojure.string/replace % #".*?([^/]+)\.(?:txt|tsv)$" "$1") files)
        get-trimmed-sheetname (fn [names] (map #(apply str (take max-sheet-name-length %)) names))
        trimmed-base-names (get-trimmed-sheetname base-names)]
    (if (apply distinct? trimmed-base-names)
      trimmed-base-names
      (get-trimmed-sheetname (drop-common-strings base-names)))))

(defn tsv-to-xls-convert
  "get tsv file as an input and create excel converted file."
  [{:keys [outfile format files encoding] :as options}]
  (let [outfile (get-output-file options)
        format (if (seq format) format "xlsx")]
    (with-open [out-stream (io/output-stream outfile)]
      (let [book (case format
                   "xls" (HSSFWorkbook.)
                   "xlsx" (SXSSFWorkbook. 100))
            sheet-names (get-sheet-names files)]
        (try
          (doseq [[i file] (map-indexed vector files)]
            (let [records (->> (if (seq encoding)
                                 (io/reader file :encoding encoding)
                                 (io/reader file))
                               line-seq
                               (map (fn [line]
                                      (clojure.string/split line #"\t" -1))))
                  sheet (.createSheet book)
                  sheet-name (nth sheet-names i)]
              (.setSheetName book i sheet-name)
              (doseq [[r row] (map-indexed vector records)]
                (let [hssf-row (.createRow sheet r)]
                  (doseq [[c cell] (map-indexed vector row)]
                    (let [hssf-cell (.createCell hssf-row c)]
                      (.setCellValue hssf-cell cell)))))))
          (.write book out-stream)
          (finally
            (.dispose book)))))))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]}
        (cli/parse-opts
         args
         [["-o" "--outfile out"  "specify output file."]
          ["-f" "--format  fmt"  "specify output format.(xls|xlsx)" :default "xlsx"]
          ["-e" "--encoding enc" "specify encoding."]
          ["-?" "--help"         "show help."]])
        {:keys [help]} options]
    (cond
     help (println summary)
     errors (println (clojure.string/join \newline errors))
     :else
     (try (tsv-to-xls-convert (assoc options :files arguments))
          (catch Exception ex (do (print-stack-trace ex)
                                  (throw ex)))))))
