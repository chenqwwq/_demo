import cn.chenqwwq.SporkApplication
import cn.chenqwwq.service.TestService
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import org.springframework.beans.factory.annotation.Autowire
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Timed
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.lang.Stepwise

/**
 *
 * @author 沽酒
 * @since 2023/06/07
 */
@ActiveProfiles("test")
@SpringBootTest(classes = SporkApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Stepwise
@RunWith(Sputnik.class)
class TestDemo1 extends Specification {

    @Autowired
    public TestService testService;


    @Timed
    def "testEcho"() {
        when: 'start'
        String ret = testService.echo(word);

        then: 'assert'
        ret != null
        !ret.isEmpty()

        and: 'set value'
        def value = ret

        expect: 'expect'
        value == expectedResult

        where: 'sucess'
        word          || expectedResult
        "HelloWorld"  || "HelloWorld"
        "HelloWorld1" || "HelloWorld1"
        "HelloWorld2" || "HelloWorld2"
        "HelloWorld3" || "HelloWorld3"
        "HelloWorld4" || "HelloWorld4"
        "HelloWorld5" || "HelloWorld5"
    }
}

