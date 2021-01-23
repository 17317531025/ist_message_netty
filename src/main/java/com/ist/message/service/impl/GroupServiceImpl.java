//package com.ist.message.service.impl;
//
//import com.ist.message.common.util.DateUtil;
//import com.ist.message.config.IstEnum;
//import com.ist.message.controller.model.*;
//import com.ist.message.dao.MsgGroupMapper;
//import com.ist.message.dao.MsgMemberMapper;
//import com.ist.message.domain.MsgGroup;
//import com.ist.message.domain.MsgGroupExample;
//import com.ist.message.domain.MsgMember;
//import com.ist.message.domain.MsgMemberExample;
//import com.ist.message.domain.vo.GetGroupInfoByIdsVo;
//import com.ist.message.domain.vo.GetGroupMemInfobyIdsVo;
//import com.ist.message.domain.vo.GetGroupMembyIdVo;
//import com.ist.message.domain.vo.MemberVo;
//import com.ist.message.service.GroupService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//
//@Service
//public class GroupServiceImpl extends BaseServiceImpl implements GroupService {
//    @Autowired
//    private MsgGroupMapper msgGroupMapper;
//    @Autowired
//    private MsgMemberMapper msgMemberMapper;
//    @Override
//    public BaseResp createGroup(CreateGroupReq req) {
//        MsgGroup msgGroup = new MsgGroup();
//        msgGroup.setAvatarurl(req.getAvatarUrl());
//        msgGroup.setCreatetime(new Date());
//        if (req.getJoinGroup()!=null){
//            msgGroup.setJoingroup(req.getJoinGroup().shortValue());
//        }else{
//            msgGroup.setJoingroup((short) 0);
//        }
//        msgGroup.setName(req.getGroupName());
//        msgGroup.setOptuserid(Long.parseLong(req.getOptUserId()));
//        msgGroup.setStatus(IstEnum.GroupStatus.NORMAL.getCode());
//        msgGroup.setUpdatetime(new Date());
//        msgGroupMapper.insertSelective(msgGroup);
//        AddGroupReq addGroupReq = new AddGroupReq();
//        addGroupReq.setGroupId(msgGroup.getGroupid().toString());
//        addGroupReq.setInviteUserId(req.getOptUserId());
//        addGroupReq.setTalker(req.getOptUserId());
//        addGroupReq.setRole(IstEnum.MemberRole.CREATER.getCode());
//        addGroup(addGroupReq);
//        return BaseResp.ok();
//    }
//
//    @Override
//    public BaseResp addGroup(AddGroupReq req) {
//        Integer seqMax = msgMemberMapper.selectMaxSeqByGroupId(Long.parseLong(req.getGroupId()));
//        if (seqMax==null){
//            seqMax = 1;
//        }else{
//            seqMax = seqMax + 1;
//        }
//        MsgMember msgMember = new MsgMember();
//        msgMember.setCreatetime(new Date());
//        msgMember.setGroupid(Long.parseLong(req.getGroupId()));
//        msgMember.setInviteuserid(Long.parseLong(req.getInviteUserId()));
//        msgMember.setOptuserid(Long.parseLong(req.getInviteUserId()));
//        msgMember.setTalker(Long.parseLong(req.getTalker()));
//        msgMember.setSeq(seqMax.shortValue());
//        if (req.getRole()!=null){
//            msgMember.setRole(req.getRole());
//        }else{
//            msgMember.setRole(IstEnum.MemberRole.MEMBER.getCode());
//        }
//        msgMember.setUpdatetime(new Date());
//        msgMemberMapper.insertSelective(msgMember);
//        return BaseResp.ok();
//    }
//
//    @Override
//    public BaseApiResp getGroupInfobyIds(GetGroupInfoByIdsReq req) {
//        MsgGroupExample examp = new MsgGroupExample();
//        List<Long> groupIds = new ArrayList<>();
//        for (String str : Arrays.asList(req.getGroupIds().split(","))){
//            groupIds.add(Long.valueOf(str));
//        }
//        examp.createCriteria().andGroupidIn(groupIds);
//        List<MsgGroup> msgGroups = msgGroupMapper.selectByExample(examp);
//        List<GetGroupInfoByIdsVo> listVo = new ArrayList<>();
//        for (MsgGroup msgGroup: msgGroups){
//            GetGroupInfoByIdsVo groupInfoByIdsVo = new GetGroupInfoByIdsVo();
//            groupInfoByIdsVo.setAvatarUrl(msgGroup.getAvatarurl());
//            groupInfoByIdsVo.setCreateTime(DateUtil.getString(msgGroup.getCreatetime(),DateUtil.PATTERN_DATE_TIME));
//            groupInfoByIdsVo.setGroupId(msgGroup.getGroupid().toString());
//            groupInfoByIdsVo.setJoinGroup(msgGroup.getJoingroup().intValue());
//            groupInfoByIdsVo.setName(msgGroup.getName());
//            groupInfoByIdsVo.setOptUserId(msgGroup.getOptuserid().toString());
//            groupInfoByIdsVo.setStatus(msgGroup.getStatus().intValue());
//            groupInfoByIdsVo.setUpdateTime(DateUtil.getString(msgGroup.getUpdatetime(),DateUtil.PATTERN_DATE_TIME));
//            listVo.add(groupInfoByIdsVo);
//        }
//        return BaseApiResp.succ(listVo);
//    }
//
//    @Override
//    public BaseApiResp getGroupMemInfobyIds(GetGroupMemInfobyIdsReq req) {
//        List<GetGroupMemInfobyIdsVo> list = new ArrayList<>();
//        for (String groupId : req.getGroupIds().split(",")){
//            GetGroupMemInfobyIdsVo getGroupMemInfobyIdsVo = new GetGroupMemInfobyIdsVo();
//            getGroupMemInfobyIdsVo.setGroupId(groupId);
//            MsgMemberExample example = new MsgMemberExample();
//            example.createCriteria().andGroupidEqualTo(Long.valueOf(groupId));
//            List<MemberVo> memberVos = new ArrayList<>();
//            List<MsgMember> msgMembers = msgMemberMapper.selectByExample(example);
//            for (MsgMember msgMember : msgMembers){
//                MemberVo memberVo = new MemberVo();
//                memberVo.setAvatarUrl(msgMember.getAvatarurl());
//                memberVo.setCreateTime(DateUtil.getString(msgMember.getCreatetime(),DateUtil.PATTERN_DATE_TIME));
//                memberVo.setInviteUserId(msgMember.getInviteuserid().toString());
//                memberVo.setLockTime(DateUtil.getString(msgMember.getLocktime(),DateUtil.PATTERN_DATE_TIME));
//                memberVo.setMemberId(msgMember.getMemberid().toString());
//                memberVo.setOptUserId(msgMember.getOptuserid().toString());
//                memberVo.setNickname(msgMember.getNickname());
//                memberVo.setRole(msgMember.getRole().intValue());
//                memberVo.setSeq(msgMember.getSeq().toString());
//                memberVo.setTalker(msgMember.getTalker().toString());
//                memberVo.setUpdateTime(DateUtil.getString(msgMember.getLocktime(),DateUtil.PATTERN_DATE_TIME));
//                memberVos.add(memberVo);
//            }
//            getGroupMemInfobyIdsVo.setMembers(memberVos);
//            list.add(getGroupMemInfobyIdsVo);
//        }
//        return BaseApiResp.succ(list);
//    }
//
//    @Override
//    public BaseApiResp getGroupMembyId(GetGroupMembyIdReq req) {
//        GetGroupMembyIdVo getGroupMembyIdVo = new GetGroupMembyIdVo();
//        getGroupMembyIdVo.setGroupId(req.getGroupId());
//        MsgMemberExample example = new MsgMemberExample();
//        example.createCriteria().andGroupidEqualTo(Long.valueOf(Long.valueOf(req.getGroupId())));
//        List<MsgMember> msgMembers = msgMemberMapper.selectByExample(example);
//        List<String> talks = new ArrayList<>();
//        for (MsgMember msgMember: msgMembers){
//            talks.add(msgMember.getTalker().toString());
//        }
//        getGroupMembyIdVo.setTalkers(talks);
//        return BaseApiResp.succ(getGroupMembyIdVo);
//    }
//}
