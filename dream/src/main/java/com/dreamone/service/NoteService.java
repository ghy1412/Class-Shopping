package com.dreamone.service;

import com.dreamone.error.BusinessException;
import com.dreamone.service.model.NoteModel;

import java.io.IOException;
import java.util.List;

public interface NoteService {

    //创建note
    NoteModel createNote(NoteModel noteModel) throws BusinessException, IOException;

    //删除note
    void deleteNote(Integer noteId);

    //展示note
    List<NoteModel> showNote(String keyword, Integer userId, Integer left, Integer right);

    //
    NoteModel getNoteById(Integer id);
}
