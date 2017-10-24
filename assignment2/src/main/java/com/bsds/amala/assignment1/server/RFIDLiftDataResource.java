package com.bsds.amala.assignment1.server;

import com.bsds.amala.assignment1.model.RFIDLiftData;
import com.bsds.amala.assignment1.model.SkiStats;
import com.bsds.amala.assignment1.server.dao.RFIDLiftDataDAO;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/lift")
public class RFIDLiftDataResource {

    @Context
    private ServletContext context;

    private RFIDLiftDataDAO rfidLiftDataDAO;

    @PostConstruct
    public void postConstruct() {
        rfidLiftDataDAO = (RFIDLiftDataDAO) context.getAttribute(LoadConfigurationListener.RFID_LIFT_DATA_DAO);
    }


    @GET
    @Path("/myvert")
    @Produces(MediaType.APPLICATION_JSON)
    public SkiStats getStats(@QueryParam("skierId") int skierId,
                             @QueryParam("dayNum") int dayNum) {
        return rfidLiftDataDAO.getSkierStats(skierId, dayNum);
    }

    @POST
    @Path("/load")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loadLiftRecord(String rfidLiftDataJson) {
        RFIDLiftData rfidLiftData = RFIDLiftData.fromJson(rfidLiftDataJson);
        rfidLiftDataDAO.insertRFIDLiftData(rfidLiftData);
        return Response.ok().build();
    }

//    private void updateStatsTable(RFIDLiftData newLiftData) {
//        int verticalMeters;
//        if (newLiftData.getLiftId() >= 1 && newLiftData.getLiftId() <= 10) {
//            verticalMeters = 200;
//        } else if (newLiftData.getLiftId() >= 11 && newLiftData.getLiftId() <= 20) {
//            verticalMeters = 300;
//        } else if (newLiftData.getLiftId() >= 21 && newLiftData.getLiftId() <= 30) {
//            verticalMeters = 400;
//        } else if (newLiftData.getLiftId() >= 31 && newLiftData.getLiftId() <= 40) {
//            verticalMeters = 500;
//        } else {
//            verticalMeters = 0;
//        }
//
//        Table table = dynamoDB.getTable("SkierStatTable");
//        GetItemSpec spec = new GetItemSpec()
//                .withPrimaryKey("skierId", newLiftData.getSkierID(), "dayNum", newLiftData.getDayNum())
//                .withAttributesToGet("skierId", "dayNum", "totalVerticalMeters", "totalLiftRides");
//
//        try {
//            Item item = table.getItem(spec);
//
//            if (item != null) {
//                UpdateItemSpec updateItemSpec = new UpdateItemSpec()
//                        .withPrimaryKey("skierId", newLiftData.getSkierID(), "dayNum", newLiftData.getDayNum())
//                        .withUpdateExpression("set totalVerticalMeters = totalVerticalMeters + :m, totalLiftRides = totalLiftRides + :r")
//                        .withValueMap(new ValueMap().withNumber(":m", verticalMeters).withNumber(":r", 1))
//                        .withReturnValues(ReturnValue.NONE);
//
//                table.updateItem(updateItemSpec);
//
//            } else {
//                try {
//                    PutItemSpec putItemSpec = new PutItemSpec()
//                            .withItem(new Item()
//                                    .withPrimaryKey("skierId", newLiftData.getSkierID(), "dayNum", newLiftData.getDayNum())
//                                    .withNumber("totalVerticalMeters", verticalMeters)
//                                    .withNumber("totalLiftRides", 1))
//                            .withConditionExpression("attribute_not_exists(skierId)")
//                            .withReturnValues(ReturnValue.NONE);
//                    table.putItem(putItemSpec);
//                } catch (ConditionalCheckFailedException e) {
//                    System.err.println("Found a race condition during put item. Item already created by another thread."
//                            + "Re-attempting updateStatsTable()");
//                    updateStatsTable(newLiftData);
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace(System.err);
//        }
//    }

//    private void deleteTable() {
//        DeleteTableRequest deleteTableRequest = new DeleteTableRequest()
//                .withTableName("RFIDLiftDataTable");
//        DeleteTableResult result = client.deleteTable(deleteTableRequest);
//    }

//    @DELETE
//    @Path("/delete")
//    public Response clearTable(@QueryParam("dayNum") int dayNum) {
//        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
//        expressionAttributeValues.put(":day", new AttributeValue().withN(String.valueOf(dayNum)));
//
//        ScanRequest scanRequest = new ScanRequest()
//                .withTableName("SkierStatTable")
//                .withFilterExpression("dayNum = :day")
//                .withExpressionAttributeValues(expressionAttributeValues);
//
//        ScanResult result = client.scan(scanRequest);
//        mapper.batchDelete(result.getItems().stream()
//                .map(stringAttributeValueMap -> {
//                    SkiStats skiStats = new SkiStats();
//                    skiStats.setSkierId(Integer.parseInt(stringAttributeValueMap.get("skierId").getN()));
//                    skiStats.setDayNum(Integer.parseInt(stringAttributeValueMap.get("dayNum").getN()));
//                    return skiStats;
//                })
//                .collect(Collectors.toList()));
//        return Response.ok().build();
//    }
}
