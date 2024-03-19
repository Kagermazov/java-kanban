package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class EpicTest {
    TaskManager testTaskManager;
    int epicId;
    int subtaskId;
    Epic testEpic;
    ArrayList<Integer> subtaskIds;


    @BeforeEach
    public void beforeEach() {
        this.testTaskManager = Managers.getDefault(new File(
                "C:\\Users\\Sergey\\dev\\java-kanban\\src\\history.csv"));
        this.testEpic = new Epic("", "", Status.NEW, TaskTypes.EPIC);
        this.epicId = testTaskManager.createEpic(this.testEpic);
        this.subtaskIds = this.testEpic.getSubtaskIds();
    }

    @Test
    public void shouldSubtasksIdListBeEmptyIfSubtaskListIsEmpty() {
        ArrayList<Integer> testList = new ArrayList<>();
        assertEquals( this.subtaskIds, testList);
    }

    @Test
    public void shouldSubtasksIdListBeNotEmptyIfSubtaskListHasOneElement() {
        this.subtaskId = this.testTaskManager.createSubtask(new Subtask(
                "", "",
                Status.DONE,
                TaskTypes.SUBTASK),
                this.epicId);
        assertFalse( this.subtaskIds.isEmpty());
    }
    //todo finish up tests for Epic class from 1.b
}