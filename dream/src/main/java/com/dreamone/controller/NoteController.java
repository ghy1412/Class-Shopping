package com.dreamone.controller;

import com.dreamone.controller.viewobject.NoteVo;
import com.dreamone.error.BusinessException;
import com.dreamone.response.CommonReturnType;
import com.dreamone.service.NoteService;
import com.dreamone.service.model.NoteModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Controller("/note")
@RequestMapping("/note")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class NoteController extends BaseController {

    @Autowired
    private NoteService noteService;

    @RequestMapping(value = "/create", method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType createNote(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "category") String category,
                                       @RequestParam(name = "body") String body,
                                       @RequestParam(name = "file", required = false) MultipartFile multipartFile,
                                       @RequestParam(name = "nfaId") Integer nfaId) throws IOException, BusinessException {

        //nfaId
        NoteModel noteModel = new NoteModel();
        noteModel.setMultipartFile(multipartFile);
        noteModel.setTitle(title);
        noteModel.setBody(body);
        noteModel.setCategory(category);
        noteModel.setNfaId(nfaId);
        NoteModel note = noteService.createNote(noteModel);
        return CommonReturnType.create(note.getId());
    }


    @RequestMapping(value = "/showNotes")
    @ResponseBody
    public CommonReturnType showNotes(@RequestParam(name = "userId", required = false) Integer userId,
                                       @RequestParam(name = "keyword", required = false) String keyword,
                                       @RequestParam(name = "left", required = false) Integer left,
                                       @RequestParam(name = "right", required = false) Integer right) throws IOException, BusinessException {

        if (left == null) left = 0;
        if (right == null) right = 5;//长度
        List<NoteModel> noteModels = noteService.showNote(keyword, userId, left, right);
        List<NoteVo> noteVoList = covertVoListFromModel(noteModels);
        return CommonReturnType.create(noteVoList);
    }

    @RequestMapping(value = "/get")
    @ResponseBody
    public CommonReturnType showNotes(@RequestParam(name = "id") Integer noteId) throws IOException, BusinessException {

        NoteModel noteById = noteService.getNoteById(noteId);
        return CommonReturnType.create(noteById);
    }


    private List<NoteVo> covertVoListFromModel(List<NoteModel> noteModels) {
        if (noteModels == null) {
            return null;
        }
        ArrayList<NoteVo> noteVos = new ArrayList<>();
        noteModels.forEach(noteModel -> {
            NoteVo noteVo = new NoteVo();
            BeanUtils.copyProperties(noteModel,noteVo);
            noteVos.add(noteVo);
        });
        return noteVos;
    }

}
