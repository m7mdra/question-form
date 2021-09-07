package com.m7mdra.questionForm

import com.m7mdra.questionForm.question.Question
import com.m7mdra.questionForm.question.QuestionType
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testValidationTrue() {
        val question = FakeQuestion(mandatory = false, id = "")
//        question.update("Hello")
        val validate = question.validate()
        println("Value:${question.value} Required:${question.mandatory} Valid:$validate")
        assert(validate)
    }
}

class FakeQuestion(title: String = "fakeQuestion", val mandatory: Boolean, val id: String) :
    Question<String?>(title, QuestionType.Input, id = id, required = mandatory) {
    override var value: String? = null

    override var hasError: Boolean = false

    override fun validate(): Boolean {
        return if (required) {
            value != null
        } else {
            true
        }
    }

    override fun collect(): Pair<String, String?> {
        return id to value
    }

    override fun update(value: String?) {
        this.value = value
    }


}