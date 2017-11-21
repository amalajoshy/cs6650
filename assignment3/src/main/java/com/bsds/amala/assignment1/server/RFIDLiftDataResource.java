package com.bsds.amala.assignment1.server;

import com.bsds.amala.assignment1.model.RFIDLiftData;
import com.bsds.amala.assignment1.server.dao.RFIDLiftDataDAO;
import com.bsds.amala.assignment1.server.metrics.Metric;
import com.bsds.amala.assignment1.server.metrics.MetricsReporter;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
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
import java.sql.SQLException;

@Path("/lift")
@Singleton
public class RFIDLiftDataResource {

    @Context
    private ServletContext context;

    private RFIDLiftDataDAO rfidLiftDataDAO;
    private MetricsReporter metricsReporter;

    @PostConstruct
    public void postConstruct() {
        rfidLiftDataDAO = (RFIDLiftDataDAO) context.getAttribute(LoadConfigurationListener.RFID_LIFT_DATA_DAO);
        metricsReporter = (MetricsReporter) context.getAttribute(LoadConfigurationListener.METRICS_REPORTER);
    }


    @GET
    @Path("/myvert")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStats(@QueryParam("skierId") int skierId,
                             @QueryParam("dayNum") int dayNum) {
        return metricsReporter.reportOperation(Metric.GET_STATS_ERRORS, Metric.GET_STATS_LATENCY, () -> {
            try {
                return Response.ok(rfidLiftDataDAO.getSkierStats(skierId, dayNum), MediaType.APPLICATION_JSON_TYPE)
                        .build();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, () -> Response.serverError().build());
    }

    @POST
    @Path("/load")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loadLiftRecord(String rfidLiftDataJson) {
        return metricsReporter.reportOperation(Metric.LOAD_LIFT_RECORD_ERRORS, Metric.LOAD_LIFT_RECORD_LATENCY, () -> {
            RFIDLiftData rfidLiftData = RFIDLiftData.fromJson(rfidLiftDataJson);
            try {
                rfidLiftDataDAO.insertRFIDLiftData(rfidLiftData);
                return Response.ok().build();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, () -> Response.serverError().build());
    }

}
