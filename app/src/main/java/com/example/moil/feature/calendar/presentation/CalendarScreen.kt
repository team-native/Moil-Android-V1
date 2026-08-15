package com.example.moil.feature.calendar.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.core.component.MoilBottomNavigation
import com.example.moil.core.component.MoilNavigationDestination
import com.example.moil.core.component.MoilTopBar
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilComponentSize
import com.example.moil.ui.theme.MoilRadius
import com.example.moil.ui.theme.MoilSpacing
import com.example.moil.ui.theme.MoilTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

private val weekdayNames = listOf(
    R.string.calendar_weekday_sunday,
    R.string.calendar_weekday_monday,
    R.string.calendar_weekday_tuesday,
    R.string.calendar_weekday_wednesday,
    R.string.calendar_weekday_thursday,
    R.string.calendar_weekday_friday,
    R.string.calendar_weekday_saturday,
)

private val calendarGroups = listOf(
    CalendarGroup(R.string.calendar_family_name),
    CalendarGroup(R.string.calendar_group_college),
    CalendarGroup(R.string.calendar_group_work),
)

private val familyMemberAvatarResources = listOf(
    R.drawable.family_avatar_mine,
    R.drawable.family_avatar_mom,
    R.drawable.family_avatar_dad,
    R.drawable.family_avatar_sibling,
)

@Composable
fun CalendarScreen(
    uiState: CalendarUiState,
    onEvent: (CalendarScreenEvent) -> Unit,
    onDestinationClick: (MoilNavigationDestination) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            MoilBottomNavigation(
                selectedDestination = MoilNavigationDestination.Calendar,
                onDestinationClick = onDestinationClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = MoilSpacing.ScreenHorizontal),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                MoilTopBar(
                    groupName = stringResource(R.string.calendar_family_name),
                    groupMenuContentDescription = stringResource(R.string.calendar_group_menu),
                    groupMemberAvatarResources = familyMemberAvatarResources,
                    searchContentDescription = stringResource(R.string.calendar_search),
                    onGroupClick = {
                        onEvent(CalendarScreenEvent.GroupMenuClicked)
                    },
                    onSearchClick = {},
                    modifier = Modifier.padding(top = MoilSpacing.HeaderTop),
                )

                if (uiState.isGroupMenuVisible) {
                    CalendarGroupMenu(
                        modifier = Modifier.padding(
                            top = MoilSpacing.HeaderTop + MoilComponentSize.TopBarItem,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(MoilSpacing.ContentTop))

            Box(modifier = Modifier.fillMaxWidth()) {
                CalendarMonthHeader(
                    displayedMonth = uiState.displayedMonth,
                    onPreviousMonthClick = {
                        onEvent(CalendarScreenEvent.PreviousMonthClicked)
                    },
                    onNextMonthClick = {
                        onEvent(CalendarScreenEvent.NextMonthClicked)
                    },
                    modifier = if (uiState.isGroupMenuVisible) {
                        Modifier.blur(5.dp)
                    } else {
                        Modifier
                    },
                )

                if (uiState.isGroupMenuVisible) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.35f)),
                    )
                }
            }

            Spacer(modifier = Modifier.height(MoilSpacing.CalendarRow))

            CalendarGrid(
                displayedMonth = uiState.displayedMonth,
                selectedDate = uiState.selectedDate,
                modifier = Modifier.weight(1f),
                onDateClick = { selectedDate ->
                    onEvent(CalendarScreenEvent.DateClicked(selectedDate))
                },
            )
        }
    }
}

@Composable
private fun CalendarGroupMenu(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .width(116.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface),
    ) {
        calendarGroups.forEachIndexed { groupIndex, calendarGroup ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(
                            if (groupIndex == 0) {
                                LocalMoilExtraColors.current.calendarEventBlue
                            } else {
                                LocalMoilExtraColors.current.calendarEventGreen
                            },
                        ),
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(calendarGroup.nameRes),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .clickable(role = Role.Button, onClick = {})
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.calendar_create_group),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}


@Composable
private fun CalendarMonthHeader(
    displayedMonth: YearMonth,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = stringResource(R.string.calendar_month_format, displayedMonth.monthValue),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = stringResource(R.string.calendar_year_format, displayedMonth.year),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        CalendarMonthNavigationButton(
            drawableRes = R.drawable.common_chevron_previous,
            contentDescription = stringResource(R.string.calendar_previous_month),
            onClick = onPreviousMonthClick,
        )

        Spacer(modifier = Modifier.width(6.dp))

        CalendarMonthNavigationButton(
            drawableRes = R.drawable.common_chevron_next,
            contentDescription = stringResource(R.string.calendar_next_month),
            onClick = onNextMonthClick,
        )
    }
}

@Composable
private fun CalendarMonthNavigationButton(
    @DrawableRes drawableRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        CalendarImageIcon(
            drawableRes = drawableRes,
            contentDescription = contentDescription,
            modifier = Modifier.size(12.dp),
        )
    }
}

@Composable
private fun CalendarGrid(
    displayedMonth: YearMonth,
    selectedDate: LocalDate,
    modifier: Modifier,
    onDateClick: (LocalDate) -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        CalendarWeekdayHeader()

        val firstDayOffset = displayedMonth.atDay(1).dayOfWeek.sundayFirstOffset()
        val visibleCellCount = firstDayOffset + displayedMonth.lengthOfMonth()
        val weekCount = if (visibleCellCount <= 35) 5 else 6
        val firstVisibleDate = displayedMonth
            .atDay(1)
            .minusDays(firstDayOffset.toLong())

        repeat(weekCount) { weekIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                repeat(7) { dayIndex ->
                    val date = firstVisibleDate.plusDays((weekIndex * 7 + dayIndex).toLong())
                    CalendarDayCell(
                        date = date,
                        isDisplayedMonth = date.month == displayedMonth.month,
                        isSelected = date == selectedDate,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { onDateClick(date) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarWeekdayHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        weekdayNames.forEach { weekdayNameRes ->
            Text(
                text = stringResource(weekdayNameRes),
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate,
    isDisplayedMonth: Boolean,
    isSelected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val selectedDateDescription = stringResource(
        R.string.calendar_selected_date,
        date.monthValue,
        date.dayOfMonth,
    )
    val dayEvents = calendarEventsFor(date)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(MoilRadius.Event))
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = selectedDateDescription },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isDisplayedMonth -> MaterialTheme.colorScheme.onSurface
                    else -> LocalMoilExtraColors.current.calendarMutedText
                },
                style = MaterialTheme.typography.labelMedium,
            )
        }

        dayEvents.take(2).forEach { calendarEvent ->
            CalendarEventBadge(calendarEvent)
        }
    }
}

@Composable
private fun CalendarEventBadge(calendarEvent: CalendarEvent) {
    val extraColors = LocalMoilExtraColors.current
    val eventColor = when (calendarEvent.color) {
        CalendarEventColor.Blue -> extraColors.calendarEventBlue
        CalendarEventColor.Green -> extraColors.calendarEventGreen
        CalendarEventColor.Yellow -> extraColors.calendarEventYellow
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(eventColor),
        )

        Spacer(modifier = Modifier.width(2.dp))

        Text(
            text = stringResource(calendarEvent.titleRes),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun CalendarImageIcon(
    @DrawableRes drawableRes: Int,
    contentDescription: String?,
    modifier: Modifier,
) {
    Image(
        painter = painterResource(drawableRes),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}

private fun DayOfWeek.sundayFirstOffset(): Int = value % 7

private fun calendarEventsFor(date: LocalDate): List<CalendarEvent> = when (date) {
    LocalDate.of(2026, 7, 5) -> listOf(CalendarEvent(R.string.calendar_event_family_meal, CalendarEventColor.Yellow))
    LocalDate.of(2026, 7, 9) -> listOf(CalendarEvent(R.string.calendar_event_trip, CalendarEventColor.Blue))
    LocalDate.of(2026, 7, 16) -> listOf(CalendarEvent(R.string.calendar_event_hospital, CalendarEventColor.Green))
    LocalDate.of(2026, 7, 22) -> listOf(
        CalendarEvent(R.string.calendar_event_trip, CalendarEventColor.Blue),
        CalendarEvent(R.string.calendar_event_day_off, CalendarEventColor.Green),
    )
    LocalDate.of(2026, 7, 28) -> listOf(CalendarEvent(R.string.calendar_event_birthday, CalendarEventColor.Yellow))
    else -> emptyList()
}

private data class CalendarGroup(
    @param:StringRes val nameRes: Int,
)

private data class CalendarEvent(
    @param:StringRes val titleRes: Int,
    val color: CalendarEventColor,
)

private enum class CalendarEventColor { Blue, Green, Yellow }

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun CalendarScreenPreview() {
    MoilTheme(darkTheme = false) {
        CalendarScreen(
            uiState = CalendarUiState(
                displayedMonth = YearMonth.of(2026, 7),
                selectedDate = LocalDate.of(2026, 7, 22),
            ),
            onEvent = {},
            onDestinationClick = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun CalendarGroupMenuPreview() {
    MoilTheme(darkTheme = false) {
        CalendarScreen(
            uiState = CalendarUiState(
                displayedMonth = YearMonth.of(2026, 7),
                selectedDate = LocalDate.of(2026, 7, 22),
                isGroupMenuVisible = true,
            ),
            onEvent = {},
            onDestinationClick = {},
        )
    }
}
