/*
 * This file is part of Discord4J.
 *
 * Discord4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Discord4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Discord4J.  If not, see <http://www.gnu.org/licenses/>.
 */
package discord4j.rest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.discordjson.json.*;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.RestTests;
import discord4j.rest.request.Router;
import discord4j.rest.util.Snowflake;
import org.junit.Before;
import org.junit.Test;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.Collections;

public class GuildServiceTest {

    private static final Logger log = Loggers.getLogger(GuildServiceTest.class);

    private static final long guild = Long.parseUnsignedLong(System.getenv("guild"));
    private static final long member = Long.parseUnsignedLong(System.getenv("member"));
    private static final long permanentRole = Long.parseUnsignedLong(System.getenv("permanentRole"));
    private static final long trashCategory = Long.parseUnsignedLong(System.getenv("trashCategory"));
    private static final long bannedUser = Long.parseUnsignedLong(System.getenv("bannedUser"));

    private GuildService guildService;
    private ChannelService channelService;

    @Before
    public void setup() {
        String token = System.getenv("token");
        boolean ignoreUnknown = !Boolean.parseBoolean(System.getenv("failUnknown"));
        ObjectMapper mapper = RestTests.getMapper(ignoreUnknown);
        Router router = RestTests.getRouter(token, mapper);
        guildService = new GuildService(router);
        channelService = new ChannelService(router);
    }

    private GuildService getGuildService() {
        return guildService;
    }

    private ChannelService getChannelService() {
        return channelService;
    }

    @Test
    public void testCreateGuild() {
        // TODO
    }

    @Test
    public void testGetGuild() {
        GuildUpdateData response = getGuildService().getGuild(guild).block();
        System.out.println(response.id());
    }

    @Test
    public void testModifyGuild() {
        GuildModifyRequest req = ImmutableGuildModifyRequest.builder()
            .region(Possible.of("us-south"))
            .build();
        getGuildService().modifyGuild(guild, req, null).block();
    }

    @Test
    public void testDeleteGuild() {
        // TODO
    }

    @Test
    public void testGetGuildChannels() {
        getGuildService().getGuildChannels(guild).then().block();
    }

    @Test
    public void testCreateGuildChannel() {
        String randomName = Long.toHexString(Double.doubleToLongBits(Math.random()));
        ChannelCreateRequest req = ImmutableChannelCreateRequest.builder()
            .name(randomName)
            .parentId(Possible.of(Snowflake.asString(trashCategory)))
            .build();
        getGuildService().createGuildChannel(guild, req, null).block();
    }

    @Test
    public void testDeleteGuildChannels() {
        getGuildService().getGuildChannels(guild)
            .filter(data -> data.parentId().get().map(parentId -> Long.parseUnsignedLong(parentId) == trashCategory).orElse(false))
            .map(ChannelData::id)
            .flatMap(id -> getChannelService().deleteChannel(Long.parseUnsignedLong(id), null))
            .then()
            .block();
    }

    @Test
    public void testModifyGuildChannelPositions() {
        // TODO
    }

    @Test
    public void testGetGuildMember() {
        getGuildService().getGuildMember(guild, member).block();
    }

    @Test
    public void testGetGuildMembers() {
        getGuildService().getGuildMembers(guild, Collections.emptyMap()).then().block();
    }

    @Test
    public void testAddGuildMember() {
        // TODO
    }

    @Test
    public void testModifyGuildMember() {
        GuildMemberModifyRequest req = ImmutableGuildMemberModifyRequest.builder()
            .nick(Possible.of("nickname"))
            .build();
        getGuildService().modifyGuildMember(guild, member, req, null).block();
    }

    @Test
    public void testModifyOwnNickname() {
        NicknameModifyData req = ImmutableNicknameModifyData.builder().nick("nickname").build();
        getGuildService().modifyOwnNickname(guild, req).block();
    }

    @Test
    public void testAddGuildMemberRole() {
        // TODO
    }

    @Test
    public void testRemoveGuildMemberRole() {
        // TODO
    }

    @Test
    public void testRemoveGuildMember() {
        // TODO
    }

    @Test
    public void testGetGuildBans() {
        getGuildService().getGuildBans(guild).then().block();
    }

    @Test
    public void testGetGuildBan() {
        getGuildService().getGuildBan(guild, bannedUser).block();
    }

    @Test
    public void testCreateGuildBan() {
        // TODO
    }

    @Test
    public void testRemoveGuildBan() {
        // TODO
    }

    @Test
    public void testGetGuildRoles() {
        getGuildService().getGuildRoles(guild).then().block();
    }

    @Test
    public void testCreateGuildRole() {
        String randomName = "test_" + Long.toHexString(Double.doubleToLongBits(Math.random()));
        RoleCreateRequest req = ImmutableRoleCreateRequest.builder()
            .name(Possible.of(randomName))
            .build();
        getGuildService().createGuildRole(guild, req, null).block();
    }

    @Test
    public void testModifyGuildRolePositions() {
        // TODO
    }

    @Test
    public void testModifyGuildRole() {
        RoleModifyRequest req = ImmutableRoleModifyRequest.builder()
            .permissions(Possible.of(0L))
            .build();
        getGuildService().modifyGuildRole(guild, permanentRole, req, null).block();
    }

    @Test
    public void testDeleteGuildRole() {
        getGuildService().getGuildRoles(guild)
            .filter(role -> role.name().startsWith("test_") || role.name().startsWith("3f"))
            .limitRequest(5)
            .flatMap(role -> getGuildService().deleteGuildRole(guild, Long.parseUnsignedLong(role.id()), null))
            .blockLast();
    }

    @Test
    public void testGetGuildPruneCount() {
        getGuildService().getGuildPruneCount(guild, Collections.emptyMap()).block();
    }

    @Test
    public void testBeginGuildPrune() {
        // shouldn't actually prune anyone because everyone in test server should have a role
        getGuildService().beginGuildPrune(guild, Collections.emptyMap(), null).block();
    }

    @Test
    public void testGetGuildVoiceRegions() {
        getGuildService().getGuildVoiceRegions(guild).then().block();
    }

    @Test
    public void testGetGuildInvites() {
        getGuildService().getGuildInvites(guild).then().block();
    }

    @Test
    public void testGetGuildIntegrations() {
        getGuildService().getGuildIntegrations(guild).then().block();
    }

    @Test
    public void testCreateGuildIntegration() {
        // TODO
    }

    @Test
    public void testModifyGuildIntegration() {
        // TODO
    }

    @Test
    public void testDeleteGuildIntegration() {
        // TODO
    }

    @Test
    public void testSyncGuildIntegration() {
        // TODO
    }

    @Test
    public void testGetGuildEmbed() {
        getGuildService().getGuildEmbed(guild).block();
    }

    @Test
    public void testModifyGuildEmbed() {
        // TODO
    }
}
