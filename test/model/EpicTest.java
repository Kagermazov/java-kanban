package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class EpicTest {
    Epic testEpic;

    @BeforeEach
    public void beforeEach() {
        this.testEpic = new Epic("", "", Status.NEW, TaskTypes.EPIC);
    }

    @Test
    public void shouldSubtasksIdListBeEmptyIfSubtaskListIsEmpty() {
        List<Integer> testList = new ArrayList<>();
        List<Integer> subtaskIds = this.testEpic.getSubtaskIds();

        assertEquals(subtaskIds, testList);
    }

    @Test
    public void shouldSubtaskIdsListBeNotEmptyWhenItHasOneElement() {
        List<Integer> testList = List.of(42);
        ArrayList<Integer> subtaskIds = this.testEpic.getSubtaskIds();

        subtaskIds.add(42);
        assertEquals(subtaskIds, testList);
    }

    @Test
    public void isToStringCorrect() {
        String expected = "Epic{subtaskIds=[], NAME='', description='', id=-1, status='NEW'}";

        assertEquals(expected, this.testEpic.toString());
    }
}