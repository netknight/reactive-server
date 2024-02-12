package io.dm

import cats.effect.IO
import weaver.Expectations.Helpers.expect
import weaver.SimpleIOSuite

object DummyTestSuite extends SimpleIOSuite {

  pureTest("non-effectful (pure) test") {
    expect("hello".length == 5)
  }

  test("dummy test2") {
    for {
      v <- IO.pure(1)
      s <- IO.pure(v + 2)
    } yield {
      expect.eql(s, 3)
    }
  }
  
  test("dummy test") {
    IO.pure(1).map(_ + 2) map { result =>
      expect.eql(result, 3)
    }
  }

}
