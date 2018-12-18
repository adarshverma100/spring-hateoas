/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.hateoas;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.hateoas.core.Relation;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * A representation model class to be rendered as specified for the media type {@code application/vnd.error+json}.
 * 
 * @see https://github.com/blongden/vnd.error
 * @author Oliver Gierke
 * @author Greg Turnquist
 */
@JsonPropertyOrder({"total"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class VndErrors extends Resources<VndErrors.VndError> {

	public static final String REL_HELP = "help";
	public static final String REL_DESCRIBES = "describes";
	public static final String REL_ABOUT = "about";

	private final List<VndError> errors;

	/**
	 * Creates a new {@link VndErrors} instance containing a single {@link VndError} with the given logref, message and
	 * optional {@link Link}s.
	 * 
	 * @param message must not be {@literal null} or empty.
	 * @param links
	 */
	public VndErrors(Integer logref, String message, String path, Link... links) {
		this(new VndError(message, path, logref, links));
	}

	/**
	 * Creates a new {@link VndErrors} wrapper for at least one {@link VndError}.
	 * 
	 * @param errors must not be {@literal null}.
	 * @param errors
	 */
	public VndErrors(VndError error, VndError... errors) {

		Assert.notNull(error, "Error must not be null");

		List<VndError> vndErrors = new ArrayList<>(errors.length + 1);
		vndErrors.add(error);
		vndErrors.addAll(Arrays.asList(errors));

		this.errors = vndErrors;
	}

	/**
	 * Creates a new {@link VndErrors} wrapper for the given {@link VndErrors}.
	 * 
	 * @param errors must not be {@literal null} or empty.
	 */
	@JsonCreator
	public VndErrors(@JsonProperty("_embedded") List<VndError> errors) {

		Assert.notNull(errors, "Errors must not be null!");
		Assert.isTrue(!errors.isEmpty(), "Errors must not be empty!");
		
		this.errors = errors;
	}

	/**
	 * Returns the underlying elements.
	 *
	 * @return the content will never be {@literal null}.
	 */
	@Override
	public Collection<VndError> getContent() {
		return this.errors;
	}

	/**
	 * Virtual attribute to generate JSON field of {@literal total}.
	 */
	public int getTotal() {
		return this.errors.size();
	}

	@Override
	public String toString() {
		
		return "VndErrors{" +
			"errors=" + errors +
			'}';
	}

	@Override
	public boolean equals(Object o) {
		
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		VndErrors vndErrors = (VndErrors) o;
		return Objects.equals(errors, vndErrors.errors);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), errors);
	}

	/**
	 * A single {@link VndError}.
	 * 
	 * @author Oliver Gierke
	 * @author Greg Turnquist
	 */
	@JsonPropertyOrder({"message", "path", "logref"})
	@Relation(collectionRelation = "errors")
	public static class VndError extends ResourceSupport {

		@Getter
		private final String message;

		@Getter
		@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
		private final String path;

		@Getter
		@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
		private final Integer logref;

		/**
		 * Creates a new {@link VndError} with the given logref, a message as well as some {@link Link}s.
		 * 
		 * @param logref must not be {@literal null} or empty.
		 * @param message must not be {@literal null} or empty.
		 * @param links
		 */
		public VndError(String message, String path, Integer logref, Link... links) {

			Assert.hasText(message, "Message must not be null or empty!");

			this.message = message;
			this.path = path;
			this.logref = logref;
			
			this.add(Arrays.asList(links));
		}

		/**
		 * Protected default constructor to allow JAXB marshalling.
		 */
		protected VndError() {

			this.message = null;
			this.path = null;
			this.logref = null;
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.hateoas.ResourceSupport#toString()
		 */
		@Override
		public String toString() {
			return String.format("VndError[message: %s, path: %s, logref: %s, links: [%s]]",
					this.message, this.path, this.logref, StringUtils.collectionToCommaDelimitedString(getLinks()));
		}

		@Override
		public boolean equals(Object o) {

			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			if (!super.equals(o)) return false;
			VndError vndError = (VndError) o;
			return Objects.equals(message, vndError.message) &&
				Objects.equals(path, vndError.path) &&
				Objects.equals(logref, vndError.logref);
		}

		@Override
		public int hashCode() {
			return Objects.hash(super.hashCode(), message, path, logref);
		}
	}
}
