
//abandoned

package com.campushub.util;

import java.util.Objects;

public record UnorderedPair<T>(T a, T b) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnorderedPair<?> that = (UnorderedPair<?>) o;
        return (Objects.equals(a, that.a) && Objects.equals(b, that.b)) || (Objects.equals(a, that.b) && Objects.equals(b, that.a));
    }

}
