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

}
