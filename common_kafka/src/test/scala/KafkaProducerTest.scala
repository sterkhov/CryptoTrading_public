import model.TestEntity
import org.junit.runner.RunWith
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner
import ru.broom.common_kafka.service.CommonKafkaProducer


@RunWith(classOf[JUnitRunner])
class KafkaProducerTest extends AnyFunSuite {
  test("Test produce") {
    val commonKafkaStream = new CommonKafkaProducer {}
   // commonKafkaStream.dropTopic("test-topic")
    commonKafkaStream.createTopic("test-topic")
    var c = 0
    while (true) {
      Thread.sleep(1000)
      c = c + 1
      commonKafkaStream.sendEntity("test-topic", new TestEntity(c))
    }
    commonKafkaStream.close
  }
}
