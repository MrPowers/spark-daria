package com.github.mrpowers.spark.daria.sql

import com.github.mrpowers.spark.daria.sql.functions._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import utest._

object HigherOrderFunctionsTest extends TestSuite with SparkSessionTestWrapper {
  import spark.implicits._

  def checkAnswer(ds: Dataset[_], expected: Seq[Row]) = {
    val actual = ds.collect.toSeq
    assert(actual == expected)
  }

  val tests = Tests {
    "transform" - {
      "handle arrays of primitive types not containing null" - {
        val df = Seq(
          Seq(
            1,
            9,
            8,
            7
          ),
          Seq(
            5,
            8,
            9,
            7,
            2
          ),
          Seq.empty,
          null
        ).toDF("i")
        checkAnswer(
          df.select(
            transform(
              col("i"),
              x => x + 1
            )
          ),
          Seq(
            Row(
              Seq(
                2,
                10,
                9,
                8
              )
            ),
            Row(
              Seq(
                6,
                9,
                10,
                8,
                3
              )
            ),
            Row(Seq.empty),
            Row(null)
          )
        )
        checkAnswer(
          df.select(
            transform(
              col("i"),
              (x, i) => x + i
            )
          ),
          Seq(
            Row(
              Seq(
                1,
                10,
                10,
                10
              )
            ),
            Row(
              Seq(
                5,
                9,
                11,
                10,
                6
              )
            ),
            Row(Seq.empty),
            Row(null)
          )
        )
      }
      "handle arrays of primitive types containing null" - {
        val df = Seq[Seq[Integer]](
          Seq(
            1,
            9,
            8,
            null,
            7
          ),
          Seq(
            5,
            null,
            8,
            9,
            7,
            2
          ),
          Seq.empty,
          null
        ).toDF("i")
        checkAnswer(
          df.select(
            transform(
              col("i"),
              x => x + 1
            )
          ),
          Seq(
            Row(
              Seq(
                2,
                10,
                9,
                null,
                8
              )
            ),
            Row(
              Seq(
                6,
                null,
                9,
                10,
                8,
                3
              )
            ),
            Row(Seq.empty),
            Row(null)
          )
        )
        checkAnswer(
          df.select(
            transform(
              col("i"),
              (x, i) => x + i
            )
          ),
          Seq(
            Row(
              Seq(
                1,
                10,
                10,
                null,
                11
              )
            ),
            Row(
              Seq(
                5,
                null,
                10,
                12,
                11,
                7
              )
            ),
            Row(Seq.empty),
            Row(null)
          )
        )
      }
      "handle arrays of non primitive types" - {
        val df = Seq(
          Seq(
            "c",
            "a",
            "b"
          ),
          Seq(
            "b",
            null,
            "c",
            null
          ),
          Seq.empty,
          null
        ).toDF("s")
        checkAnswer(
          df.select(
            transform(
              col("s"),
              x =>
                concat(
                  x,
                  x
              )
            )
          ),
          Seq(
            Row(
              Seq(
                "cc",
                "aa",
                "bb"
              )
            ),
            Row(
              Seq(
                "bb",
                null,
                "cc",
                null
              )
            ),
            Row(Seq.empty),
            Row(null)
          )
        )
        checkAnswer(
          df.select(
            transform(
              col("s"),
              (x, i) =>
                concat(
                  x,
                  i
              )
            )
          ),
          Seq(
            Row(
              Seq(
                "c0",
                "a1",
                "b2"
              )
            ),
            Row(
              Seq(
                "b0",
                null,
                "c2",
                null
              )
            ),
            Row(Seq.empty),
            Row(null)
          )
        )
      }
      "handle special cases" - {
        val df = Seq(
          Seq(
            "c",
            "a",
            "b"
          ),
          Seq(
            "b",
            null,
            "c",
            null
          ),
          Seq.empty,
          null
        ).toDF("arg")
        checkAnswer(
          df.select(
            transform(
              col("arg"),
              arg => arg
            )
          ),
          Seq(
            Row(
              Seq(
                "c",
                "a",
                "b"
              )
            ),
            Row(
              Seq(
                "b",
                null,
                "c",
                null
              )
            ),
            Row(Seq.empty),
            Row(null)
          )
        )
        checkAnswer(
          df.select(
            transform(
              col("arg"),
              _ => col("arg")
            )
          ),
          Seq(
            Row(
              Seq(
                Seq(
                  "c",
                  "a",
                  "b"
                ),
                Seq(
                  "c",
                  "a",
                  "b"
                ),
                Seq(
                  "c",
                  "a",
                  "b"
                )
              )
            ),
            Row(
              Seq(
                Seq(
                  "b",
                  null,
                  "c",
                  null
                ),
                Seq(
                  "b",
                  null,
                  "c",
                  null
                ),
                Seq(
                  "b",
                  null,
                  "c",
                  null
                ),
                Seq(
                  "b",
                  null,
                  "c",
                  null
                )
              )
            ),
            Row(Seq.empty),
            Row(null)
          )
        )
        checkAnswer(
          df.select(
            transform(
              col("arg"),
              x =>
                concat(
                  col("arg"),
                  array(x)
              )
            )
          ),
          Seq(
            Row(
              Seq(
                Seq(
                  "c",
                  "a",
                  "b",
                  "c"
                ),
                Seq(
                  "c",
                  "a",
                  "b",
                  "a"
                ),
                Seq(
                  "c",
                  "a",
                  "b",
                  "b"
                )
              )
            ),
            Row(
              Seq(
                Seq(
                  "b",
                  null,
                  "c",
                  null,
                  "b"
                ),
                Seq(
                  "b",
                  null,
                  "c",
                  null,
                  null
                ),
                Seq(
                  "b",
                  null,
                  "c",
                  null,
                  "c"
                ),
                Seq(
                  "b",
                  null,
                  "c",
                  null,
                  null
                )
              )
            ),
            Row(Seq.empty),
            Row(null)
          )
        )
      }
    }
    "exists" - {
      "handle arrays of primtive types not containing null" - {
        val df = Seq(
          Seq(
            1,
            9,
            8,
            7
          ),
          Seq(
            5,
            9,
            7
          ),
          Seq.empty,
          null
        ).toDF("i")
        checkAnswer(
          df.select(
            exists(
              col("i"),
              _ % 2 === 0
            )
          ),
          Seq(
            Row(true),
            Row(false),
            Row(false),
            Row(null)
          )
        )
      }
      "handle arrays of primitive types containing null" - {
        val df = Seq[Seq[Integer]](
          Seq(
            1,
            9,
            8,
            null,
            7
          ),
          Seq(
            5,
            null,
            null,
            9,
            7,
            null
          ),
          Seq.empty,
          null
        ).toDF("i")
        checkAnswer(
          df.select(
            exists(
              col("i"),
              _ % 2 === 0
            )
          ),
          Seq(
            Row(true),
            Row(false),
            Row(false),
            Row(null)
          )
        )
      }
      "handle arrays of non primitive types" - {
        val df = Seq(
          Seq(
            "c",
            "a",
            "b"
          ),
          Seq(
            "b",
            null,
            "c",
            null
          ),
          Seq.empty,
          null
        ).toDF("s")
        checkAnswer(
          df.select(
            exists(
              col("s"),
              x => x.isNull
            )
          ),
          Seq(
            Row(false),
            Row(true),
            Row(false),
            Row(null)
          )
        )
      }
      "not handle invalid inputs" - {
        val df = Seq(
          (
            Seq(
              "c",
              "a",
              "b"
            ),
            1
          ),
          (
            Seq(
              "b",
              null,
              "c",
              null
            ),
            2
          ),
          (Seq.empty, 3),
          (null, 4)
        ).toDF(
          "s",
          "i"
        )
        val ex2a = intercept[AnalysisException] {
          df.select(
            exists(
              col("i"),
              x => x
            )
          )
        }
        assert(ex2a.getMessage.contains("data type mismatch: argument 1 requires array type"))
        val ex3a = intercept[AnalysisException] {
          df.select(
            exists(
              col("s"),
              x => x
            )
          )
        }
        assert(ex3a.getMessage.contains("data type mismatch: argument 2 requires boolean type"))
      }
    }
    "filter" - {
      "handle arrays of primitive types not containing null" - {
        val df = Seq(
          Seq(
            1,
            9,
            8,
            7
          ),
          Seq(
            5,
            8,
            9,
            7,
            2
          ),
          Seq.empty,
          null
        ).toDF("i")
        checkAnswer(
          df.select(
            filter(
              col("i"),
              _ % 2 === 0
            )
          ),
          Seq(
            Row(Seq(8)),
            Row(
              Seq(
                8,
                2
              )
            ),
            Row(Seq.empty),
            Row(null)
          )
        )
      }
      "handle arrays of primitive types containing null" - {
        val df = Seq[Seq[Integer]](
          Seq(
            1,
            9,
            8,
            null,
            7
          ),
          Seq(
            5,
            null,
            8,
            9,
            7,
            2
          ),
          Seq.empty,
          null
        ).toDF("i")
        checkAnswer(
          df.select(
            filter(
              col("i"),
              _ % 2 === 0
            )
          ),
          Seq(
            Row(Seq(8)),
            Row(
              Seq(
                8,
                2
              )
            ),
            Row(Seq.empty),
            Row(null)
          )
        )
      }
      "handle arrays of non primitive types" - {
        val df = Seq(
          Seq(
            "c",
            "a",
            "b"
          ),
          Seq(
            "b",
            null,
            "c",
            null
          ),
          Seq.empty,
          null
        ).toDF("s")
        checkAnswer(
          df.select(
            filter(
              col("s"),
              x => x.isNotNull
            )
          ),
          Seq(
            Row(
              Seq(
                "c",
                "a",
                "b"
              )
            ),
            Row(
              Seq(
                "b",
                "c"
              )
            ),
            Row(Seq.empty),
            Row(null)
          )
        )
      }
      "not handle invalid cases" - {
        val df = Seq(
          (
            Seq(
              "c",
              "a",
              "b"
            ),
            1
          ),
          (
            Seq(
              "b",
              null,
              "c",
              null
            ),
            2
          ),
          (Seq.empty, 3),
          (null, 4)
        ).toDF(
          "s",
          "i"
        )
        val ex2a = intercept[AnalysisException] {
          df.select(
            filter(
              col("i"),
              x => x
            )
          )
        }
        assert(ex2a.getMessage.contains("data type mismatch: argument 1 requires array type"))
        val ex3a = intercept[AnalysisException] {
          df.select(
            filter(
              col("s"),
              x => x
            )
          )
        }
        assert(ex3a.getMessage.contains("data type mismatch: argument 2 requires boolean type"))
      }
    }
    "aggregate" - {
      "handle arrays of primitive types not containing null" - {
        val df = Seq(
          Seq(
            1,
            9,
            8,
            7
          ),
          Seq(
            5,
            8,
            9,
            7,
            2
          ),
          Seq.empty,
          null
        ).toDF("i")
        checkAnswer(
          df.select(
            aggregate(
              col("i"),
              lit(0),
              (acc, x) => acc + x
            )
          ),
          Seq(
            Row(25),
            Row(31),
            Row(0),
            Row(null)
          )
        )
        checkAnswer(
          df.select(
            aggregate(
              col("i"),
              lit(0),
              (acc, x) => acc + x,
              _ * 10
            )
          ),
          Seq(
            Row(250),
            Row(310),
            Row(0),
            Row(null)
          )
        )
      }
      "handle arrays of primitive types containing null" - {
        val df = Seq[Seq[Integer]](
          Seq(
            1,
            9,
            8,
            7
          ),
          Seq(
            5,
            null,
            8,
            9,
            7,
            2
          ),
          Seq.empty,
          null
        ).toDF("i")
        checkAnswer(
          df.select(
            aggregate(
              col("i"),
              lit(0),
              (acc, x) => acc + x
            )
          ),
          Seq(
            Row(25),
            Row(null),
            Row(0),
            Row(null)
          )
        )
        checkAnswer(
          df.select(
            aggregate(
              col("i"),
              lit(0),
              (acc, x) => acc + x,
              acc =>
                coalesce(
                  acc,
                  lit(0)
                ) * 10
            )
          ),
          Seq(
            Row(250),
            Row(0),
            Row(0),
            Row(null)
          )
        )
      }
      "handle arrays of non primitive types" - {
        val df = Seq(
          (
            Seq(
              "c",
              "a",
              "b"
            ),
            "a"
          ),
          (
            Seq(
              "b",
              null,
              "c",
              null
            ),
            "b"
          ),
          (Seq.empty, "c"),
          (null, "d")
        ).toDF(
          "ss",
          "s"
        )
        checkAnswer(
          df.select(
            aggregate(
              col("ss"),
              col("s"),
              (acc, x) =>
                concat(
                  acc,
                  x
              )
            )
          ),
          Seq(
            Row("acab"),
            Row(null),
            Row("c"),
            Row(null)
          )
        )
        checkAnswer(
          df.select(
            aggregate(
              col("ss"),
              col("s"),
              (acc, x) =>
                concat(
                  acc,
                  x
              ),
              acc =>
                coalesce(
                  acc,
                  lit("")
              )
            )
          ),
          Seq(
            Row("acab"),
            Row(""),
            Row("c"),
            Row(null)
          )
        )
      }
      "not handle invalid cases" - {
        val df = Seq(
          (
            Seq(
              "c",
              "a",
              "b"
            ),
            1
          ),
          (
            Seq(
              "b",
              null,
              "c",
              null
            ),
            2
          ),
          (Seq.empty, 3),
          (null, 4)
        ).toDF(
          "s",
          "i"
        )
        val ex3a = intercept[AnalysisException] {
          df.select(
            aggregate(
              col("i"),
              lit(0),
              (acc, x) => x
            )
          )
        }
        assert(ex3a.getMessage.contains("data type mismatch: argument 1 requires array type"))
        val ex4a = intercept[AnalysisException] {
          df.select(
            aggregate(
              col("s"),
              lit(0),
              (acc, x) => x
            )
          )
        }
        assert(ex4a.getMessage.contains("data type mismatch: argument 3 requires int type"))
      }
    }
    "zip_with" - {
      "handle arrays of primitive types" - {
        val df1 = Seq[(Seq[Integer], Seq[Integer])](
          (
            Seq(
              9001,
              9002,
              9003
            ),
            Seq(
              4,
              5,
              6
            )
          ),
          (
            Seq(
              1,
              2
            ),
            Seq(
              3,
              4
            )
          ),
          (Seq.empty, Seq.empty),
          (null, null)
        ).toDF(
          "val1",
          "val2"
        )
        val df2 = Seq[(Seq[Integer], Seq[Long])](
          (
            Seq(
              1,
              null,
              3
            ),
            Seq(
              1L,
              2L
            )
          ),
          (
            Seq(
              1,
              2,
              3
            ),
            Seq(
              4L,
              11L
            )
          )
        ).toDF(
          "val1",
          "val2"
        )
        val expectedValue1 = Seq(
          Row(
            Seq(
              9005,
              9007,
              9009
            )
          ),
          Row(
            Seq(
              4,
              6
            )
          ),
          Row(Seq.empty),
          Row(null)
        )
        checkAnswer(
          df1.select(
            zip_with(
              df1("val1"),
              df1("val2"),
              (x, y) => x + y
            )
          ),
          expectedValue1
        )
        val expectedValue2 = Seq(
          Row(
            Seq(
              Row(
                1L,
                1
              ),
              Row(
                2L,
                null
              ),
              Row(
                null,
                3
              )
            )
          ),
          Row(
            Seq(
              Row(
                4L,
                1
              ),
              Row(
                11L,
                2
              ),
              Row(
                null,
                3
              )
            )
          )
        )
        checkAnswer(
          df2.select(
            zip_with(
              df2("val1"),
              df2("val2"),
              (x, y) =>
                struct(
                  y,
                  x
              )
            )
          ),
          expectedValue2
        )
      }
      "handle arrays of non primtive types" - {
        val df = Seq(
          (
            Seq("a"),
            Seq(
              "x",
              "y",
              "z"
            )
          ),
          (
            Seq(
              "a",
              null
            ),
            Seq(
              "x",
              "y"
            )
          ),
          (Seq.empty[String], Seq.empty[String]),
          (
            Seq(
              "a",
              "b",
              "c"
            ),
            null
          )
        ).toDF(
          "val1",
          "val2"
        )
        val expectedValue1 = Seq(
          Row(
            Seq(
              Row(
                "x",
                "a"
              ),
              Row(
                "y",
                null
              ),
              Row(
                "z",
                null
              )
            )
          ),
          Row(
            Seq(
              Row(
                "x",
                "a"
              ),
              Row(
                "y",
                null
              )
            )
          ),
          Row(Seq.empty),
          Row(null)
        )
        checkAnswer(
          df.select(
            zip_with(
              col("val1"),
              col("val2"),
              (x, y) =>
                struct(
                  y,
                  x
              )
            )
          ),
          expectedValue1
        )
      }
      "not handle invalid cases" - {
        val df = Seq(
          (
            Seq(
              "c",
              "a",
              "b"
            ),
            Seq(
              "x",
              "y",
              "z"
            ),
            1
          ),
          (
            Seq(
              "b",
              null,
              "c",
              null
            ),
            Seq("x"),
            2
          ),
          (
            Seq.empty,
            Seq(
              "x",
              "z"
            ),
            3
          ),
          (
            null,
            Seq(
              "x",
              "z"
            ),
            4
          )
        ).toDF(
          "a1",
          "a2",
          "i"
        )
        val ex3a = intercept[AnalysisException] {
          df.select(
            zip_with(
              col("i"),
              col("a2"),
              (acc, x) => x
            )
          )
        }
        assert(ex3a.getMessage.contains("data type mismatch: argument 1 requires array type"))
      }
    }
  }
}
