package jm.tools.service;

import jm.tools.service.message.IRequestMessage;
import jm.tools.service.message.IResponseMessage;
import jm.tools.service.util.ServiceException;

public interface IService {
	public void doService(IRequestMessage request, IResponseMessage response) throws ServiceException;
}
