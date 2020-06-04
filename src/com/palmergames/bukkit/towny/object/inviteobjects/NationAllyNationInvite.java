package com.palmergames.bukkit.towny.object.inviteobjects;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.invites.Invite;
import com.palmergames.bukkit.towny.invites.TownyInviteReceiver;
import com.palmergames.bukkit.towny.invites.TownyInviteSender;
import com.palmergames.bukkit.towny.object.Nation;

public class NationAllyNationInvite implements Invite {

	public NationAllyNationInvite(String directsender, TownyInviteSender sender, TownyInviteReceiver receiver) {
		this.directsender = directsender;
		this.sender = sender;
		this.receiver = receiver;
	}

	private String directsender;
	private TownyInviteReceiver receiver;
	private TownyInviteSender sender;

	@Override
	public String getDirectSender() {
		return directsender;
	}

	@Override
	public TownyInviteReceiver getReceiver() {
		return receiver;
	}

	@Override
	public TownyInviteSender getSender() {
		return sender;
	}

	@Override
	public void accept() throws TownyException {
			Nation receivernation = (Nation) getReceiver();
			Nation sendernation = (Nation) getSender();
			receivernation.addAlly(sendernation);
			sendernation.addAlly(receivernation);
			TownyMessaging.sendPrefixedNationMessage(receivernation, String.format(TownySettings.getLangString("msg_added_ally"), sendernation.getName()));
			TownyMessaging.sendPrefixedNationMessage(sendernation, String.format(TownySettings.getLangString("msg_accept_ally"), receivernation.getName()));
			receivernation.deleteReceivedInvite(this);
			sendernation.deleteSentAllyInvite(this);
			TownyUniverse.getInstance().getDataSource().saveNation(receivernation);
			TownyUniverse.getInstance().getDataSource().saveNation(sendernation);
	}

	@Override
	public void decline(boolean fromSender) {
		Nation receivernation = (Nation) getReceiver();
		Nation sendernation = (Nation) getSender();
		receivernation.deleteReceivedInvite(this);
		sendernation.deleteSentAllyInvite(this);
		if (!fromSender) {
			TownyMessaging.sendPrefixedNationMessage(sendernation, String.format(TownySettings.getLangString("msg_deny_ally"), TownySettings.getLangString("nation_sing") + ": " + receivernation.getName()));
		} else {
			TownyMessaging.sendPrefixedNationMessage(receivernation, String.format(TownySettings.getLangString("nation_revoke_ally"), sendernation.getName()));
		}
	}
}
