package io.teknek.gossip.spring.example;

import io.teknek.gossip.LocalMember;
import io.teknek.gossip.manager.GossipManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
public class GossipSpringExampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(GossipSpringExampleApplication.class, args);
  }

  @RestController
  static class StatusPageController {

    private final GossipManager gossipManager;

    StatusPageController(GossipManager gossipManager) {
      this.gossipManager = gossipManager;
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
      return """
              <!doctype html>
              <html>
              <head>
                <title>Gossip Spring Example</title>
                <style>
                  body { font-family: system-ui, sans-serif; margin: 2rem; color: #1f2937; }
                  h1 { margin-bottom: 0.25rem; }
                  .node { border: 1px solid #d1d5db; border-radius: 0.5rem; padding: 1rem; margin: 1rem 0; }
                  .up { border-left: 0.5rem solid #16a34a; }
                  .down { border-left: 0.5rem solid #dc2626; }
                  code { background: #f3f4f6; padding: 0.125rem 0.25rem; border-radius: 0.25rem; }
                </style>
                <meta http-equiv="refresh" content="2">
              </head>
              <body>
              """
              + "<h1>Gossip Cluster " + escape(gossipManager.getMyself().getClusterName()) + "</h1>"
              + "<p>This page refreshes every 2 seconds. JSON status is at <code>/gossip/status</code>.</p>"
              + section("Self", List.of(gossipManager.getMyself()), "node")
              + section("Live Members", gossipManager.getLiveMembers(), "node up")
              + section("Dead Members", gossipManager.getDeadMembers(), "node down")
              + "</body></html>";
    }

    private String section(String title, List<LocalMember> members, String cssClass) {
      StringBuilder html = new StringBuilder("<h2>").append(escape(title)).append("</h2>");
      if (members.isEmpty()) {
        return html.append("<p>None</p>").toString();
      }
      for (LocalMember member : members) {
        html.append("<div class=\"").append(cssClass).append("\">")
                .append("<strong>").append(escape(member.getId())).append("</strong><br>")
                .append("<code>").append(escape(member.getUri().toASCIIString())).append("</code><br>")
                .append("Heartbeat: ").append(member.getHeartbeat())
                .append("</div>");
      }
      return html.toString();
    }

    private String escape(String value) {
      if (value == null) {
        return "";
      }
      return value.replace("&", "&amp;")
              .replace("<", "&lt;")
              .replace(">", "&gt;")
              .replace("\"", "&quot;");
    }
  }
}
