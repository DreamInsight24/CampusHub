package com.campushub.mapper;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Param;

import com.campushub.common.enums.DemandStat;
import com.campushub.common.enums.DemandApplicationStatus;
import com.campushub.common.enums.DemandType;
import com.campushub.dto.demand.DemandQueryDTO;
import com.campushub.entity.demand.Demand;
import com.campushub.entity.demand.DemandApplication;
import com.campushub.entity.demand.ExpressDemandDetail;
import com.campushub.entity.demand.SecondhandDemandDetail;
import com.campushub.entity.demand.TeamupDemandDetail;
import com.campushub.entity.demand.TutoringDemandDetail;
import com.campushub.vo.demand.DemandApplicationVO;
import com.campushub.vo.demand.DemandListVO;
import com.campushub.vo.demand.DemandDetailVO;

public interface DemandMapper {
    //需要的接口自行补充
    int insertDemand(Demand demand);

    int updateDemand(Demand demand);

    int updateDemandStat(@Param("uuid") UUID uuid, @Param("stat") DemandStat stat);

    int updateDemandStatByPublisher(
            @Param("uuid") UUID uuid,
            @Param("publisher_uuid") UUID publisher_uuid,
            @Param("stat") DemandStat stat);

    int updateDemandTaker(@Param("uuid") UUID uuid, @Param("taker_uuid") UUID taker_uuid);

    int updateTeamupCurrentMembers(@Param("demand_uuid") UUID demand_uuid);

    int deleteDemandByUuid(@Param("uuid") UUID uuid);

    Demand selectDemandByUuid(@Param("uuid") UUID uuid);

    List<Demand> selectDemandsByPublisherUuid(@Param("publisher_uuid") UUID publisher_uuid);

    List<Demand> selectDemandsByTakerUuid(@Param("taker_uuid") UUID taker_uuid);

    List<Demand> selectDemandsByType(@Param("type") DemandType type);

    List<Demand> selectDemandsByStat(@Param("stat") DemandStat stat);

    List<Demand> selectLatestDemands(@Param("limit") int limit);

    List<DemandListVO> searchDemands(@Param("query") DemandQueryDTO query, @Param("offset") int offset);

    long countDemands(@Param("query") DemandQueryDTO query);

    DemandDetailVO selectDemandDetailByUuid(@Param("uuid") UUID uuid);

    List<DemandDetailVO> selectPublishedDemandDetails(@Param("user_uuid") UUID user_uuid);

    List<DemandDetailVO> selectAcceptedDemandDetails(@Param("user_uuid") UUID user_uuid);

    List<DemandDetailVO> selectFavoriteDemandDetails(@Param("user_uuid") UUID user_uuid);

    int insertExpressDemandDetail(ExpressDemandDetail detail);

    int insertSecondhandDemandDetail(SecondhandDemandDetail detail);

    int insertTutoringDemandDetail(TutoringDemandDetail detail);

    int insertTeamupDemandDetail(TeamupDemandDetail detail);

    ExpressDemandDetail selectExpressDemandDetailByDemandUuid(@Param("demand_uuid") UUID demand_uuid);

    SecondhandDemandDetail selectSecondhandDemandDetailByDemandUuid(@Param("demand_uuid") UUID demand_uuid);

    TutoringDemandDetail selectTutoringDemandDetailByDemandUuid(@Param("demand_uuid") UUID demand_uuid);

    TeamupDemandDetail selectTeamupDemandDetailByDemandUuid(@Param("demand_uuid") UUID demand_uuid);

    int updateExpressDemandDetail(ExpressDemandDetail detail);

    int updateSecondhandDemandDetail(SecondhandDemandDetail detail);

    int updateTutoringDemandDetail(TutoringDemandDetail detail);

    int updateTeamupDemandDetail(TeamupDemandDetail detail);

    int deleteDemandDetailByDemandUuid(@Param("demand_uuid") UUID demand_uuid);

    int insertDemandApplication(DemandApplication application);

    DemandApplication selectDemandApplicationByUuid(@Param("uuid") UUID uuid);

    DemandApplicationVO selectDemandApplicationVOByUuid(@Param("uuid") UUID uuid);

    DemandApplicationVO selectDemandApplicationByDemandAndApplicant(
            @Param("demand_uuid") UUID demand_uuid,
            @Param("applicant_uuid") UUID applicant_uuid);

    List<DemandApplicationVO> selectDemandApplications(@Param("demand_uuid") UUID demand_uuid);

    int updateDemandApplicationStatus(
            @Param("uuid") UUID uuid,
            @Param("status") DemandApplicationStatus status);

    int expireOtherPendingApplications(
            @Param("demand_uuid") UUID demand_uuid,
            @Param("accepted_uuid") UUID accepted_uuid);

    int expirePendingApplicationsByDemand(@Param("demand_uuid") UUID demand_uuid);

    int insertDemandFavorite(
            @Param("user_uuid") UUID user_uuid,
            @Param("demand_uuid") UUID demand_uuid);

    int deleteDemandFavorite(
            @Param("user_uuid") UUID user_uuid,
            @Param("demand_uuid") UUID demand_uuid);
}
